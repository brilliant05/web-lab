package com.boda.springboot.service.impl;

import com.boda.springboot.common.Constant;
import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.AnswerSubmitDTO;
import com.boda.springboot.dto.QuestionPageQueryDTO;
import com.boda.springboot.dto.QuestionSubmitDTO;
import com.boda.springboot.dto.QuestionUpdateDTO;
import com.boda.springboot.entity.Answer;
import com.boda.springboot.entity.AnswerLike;
import com.boda.springboot.entity.Question;
import com.boda.springboot.exception.ServiceException;
import com.boda.springboot.mapper.AnswerLikeMapper;
import com.boda.springboot.mapper.AnswerMapper;
import com.boda.springboot.mapper.QuestionMapper;
import com.boda.springboot.service.NotificationService;
import com.boda.springboot.service.QuestionService;
import com.boda.springboot.utils.QiNiuUtil;
import com.boda.springboot.vo.AnswerVO;
import com.boda.springboot.vo.QuestionDetailVO;
import com.boda.springboot.vo.QuestionVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 问答Service实现类
 */
@Service
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private AnswerLikeMapper answerLikeMapper;

    @Autowired
    private QiNiuUtil qiNiuUtil;

    @Autowired(required = false)
    private NotificationService notificationService;

    @Override
    @Transactional
    public void submitQuestion(QuestionSubmitDTO submitDTO, MultipartFile[] images, Long studentId) {
        log.info("学生提交问题 - 学生ID: {}, 标题: {}", studentId, submitDTO.getQuestionTitle());

        // 参数校验
        if (submitDTO.getQuestionTitle() == null || submitDTO.getQuestionTitle().trim().isEmpty()) {
            throw new ServiceException("问题标题不能为空");
        }
        if (submitDTO.getQuestionContent() == null || submitDTO.getQuestionContent().trim().isEmpty()) {
            throw new ServiceException("问题内容不能为空");
        }

        // 上传图片
        String imageUrls = null;
        try {
            imageUrls = qiNiuUtil.uploadImages(images, "question");
        } catch (IOException e) {
            log.error("图片上传失败", e);
            throw new ServiceException("图片上传失败: " + e.getMessage());
        }

        // 保存问题
        Question question = new Question();
        BeanUtils.copyProperties(submitDTO, question);
        question.setStudentId(studentId);
        question.setImageUrls(imageUrls);

        questionMapper.save(question);
        log.info("问题提交成功 - 问题ID: {}", question.getQuestionId());
    }

    @Override
    public PageResult pageQuery(QuestionPageQueryDTO queryDTO) {
        log.info("分页查询问题列表 - 查询条件: {}", queryDTO);

        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<QuestionVO> page = (Page<QuestionVO>) questionMapper.selectPageList(
                queryDTO.getCourseId(),
                queryDTO.getTeacherId(),
                queryDTO.getIsAnswered(),
                queryDTO.getKeyword()
        );

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public QuestionDetailVO getQuestionDetail(Long questionId, Long currentUserId) {
        log.info("查询问题详情 - 问题ID: {}", questionId);

        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new ServiceException("问题不存在");
        }

        // 增加浏览次数
        questionMapper.increaseViewCount(questionId);

        // 查询问题详情
        QuestionDetailVO detailVO = questionMapper.selectDetailById(questionId);

        // 查询所有回答
        List<AnswerVO> answers = answerMapper.selectByQuestionId(questionId, currentUserId);
        detailVO.setAnswers(answers);

        return detailVO;
    }

    @Override
    @Transactional
    public void updateQuestion(Long questionId, QuestionUpdateDTO updateDTO, Long userId, String userRole) {
        log.info("更新问题 - 问题ID: {}, 用户ID: {}, 角色: {}", questionId, userId, userRole);

        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new ServiceException("问题不存在");
        }

        // 权限校验：管理员可以修改任意问题，学生只能修改自己未回答的问题
        if (!Constant.ROLE_ADMIN.equals(userRole)) {
            if (!question.getStudentId().equals(userId)) {
                throw new ServiceException("只能修改自己提交的问题");
            }
            if (question.getIsAnswered() == 1) {
                throw new ServiceException("回答问题已回答，不能修改");
            }
        }

        Question updateQuestion = new Question();
        updateQuestion.setQuestionId(questionId);
        updateQuestion.setQuestionTitle(updateDTO.getQuestionTitle());
        updateQuestion.setQuestionContent(updateDTO.getQuestionContent());
        updateQuestion.setTags(updateDTO.getTags());

        questionMapper.update(updateQuestion);
        log.info("问题更新成功");
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId, Long userId, String userRole) {
        log.info("删除问题 - 问题ID: {}, 用户ID: {}, 角色: {}", questionId, userId, userRole);

        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new ServiceException("问题不存在");
        }

        // 权限校验：管理员可以删除任意问题，学生只能删除自己的问题
        if (!Constant.ROLE_ADMIN.equals(userRole) && !question.getStudentId().equals(userId)) {
            throw new ServiceException("只能删除自己提交的问题");
        }

        questionMapper.deleteById(questionId);
        log.info("问题删除成功");
    }

    @Override
    public PageResult getMyQuestions(Integer pageNum, Integer pageSize, Integer isAnswered, Long studentId) {
        log.info("查询我的提问列表 - 学生ID: {}", studentId);

        PageHelper.startPage(pageNum, pageSize);
        Page<QuestionVO> page = (Page<QuestionVO>) questionMapper.selectMyQuestions(studentId, isAnswered);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public PageResult getPendingQuestions(Integer pageNum, Integer pageSize, Long courseId, Long teacherId) {
        log.info("查询待回答问题列表 - 教师ID: {}", teacherId);

        PageHelper.startPage(pageNum, pageSize);
        Page<QuestionVO> page = (Page<QuestionVO>) questionMapper.selectPendingQuestions(teacherId, courseId);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void answerQuestion(Long questionId, AnswerSubmitDTO submitDTO, MultipartFile[] images, Long teacherId) {
        log.info("教师回答问题 - 问题ID: {}, 教师ID: {}", questionId, teacherId);

        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new ServiceException("问题不存在");
        }

        if (!question.getTeacherId().equals(teacherId)) {
            throw new ServiceException("只能回答提给自己的问题");
        }

        // 上传图片
        String imageUrls = null;
        try {
            imageUrls = qiNiuUtil.uploadImages(images, "answer");
        } catch (IOException e) {
            log.error("图片上传失败", e);
            throw new ServiceException("图片上传失败: " + e.getMessage());
        }

        // 保存回答
        Answer answer = new Answer();
        answer.setQuestionId(questionId);
        answer.setTeacherId(teacherId);
        answer.setAnswerContent(submitDTO.getAnswerContent());
        answer.setImageUrls(imageUrls);

        answerMapper.save(answer);

        // 更新问题状态
        questionMapper.increaseAnswerCount(questionId);
        questionMapper.updateAnsweredStatus(questionId, 1);

        // 发送通知给提问学生
        if (notificationService != null) {
            try {
                notificationService.createAnswerNotification(question.getStudentId(), teacherId, questionId, question.getQuestionTitle());
            } catch (Exception e) {
                log.error("发送通知失败", e);
            }
        }

        log.info("回答成功 - 回答ID: {}", answer.getAnswerId());
    }

    @Override
    @Transactional
    public void updateAnswer(Long answerId, String answerContent, Long userId, String userRole) {
        log.info("更新回答 - 回答ID: {}, 用户ID: {}, 角色: {}", answerId, userId, userRole);

        Answer answer = answerMapper.selectById(answerId);
        if (answer == null) {
            throw new ServiceException("回答不存在");
        }

        // 权限校验：管理员可以修改任意回答，教师只能修改自己的回答
        if (!Constant.ROLE_ADMIN.equals(userRole) && !answer.getTeacherId().equals(userId)) {
            throw new ServiceException("只能修改自己的回答");
        }

        Answer updateAnswer = new Answer();
        updateAnswer.setAnswerId(answerId);
        updateAnswer.setAnswerContent(answerContent);

        answerMapper.update(updateAnswer);
        log.info("回答更新成功");
    }

    @Override
    @Transactional
    public void deleteAnswer(Long answerId, Long userId, String userRole) {
        log.info("删除回答 - 回答ID: {}, 用户ID: {}, 角色: {}", answerId, userId, userRole);

        Answer answer = answerMapper.selectById(answerId);
        if (answer == null) {
            throw new ServiceException("回答不存在");
        }

        // 权限校验：管理员可以删除任意回答，教师只能删除自己的回答
        if (!Constant.ROLE_ADMIN.equals(userRole) && !answer.getTeacherId().equals(userId)) {
            throw new ServiceException("只能删除自己的回答");
        }

        answerMapper.deleteById(answerId);

        // 更新问题的回答数量
        questionMapper.decreaseAnswerCount(answer.getQuestionId());

        // 检查是否还有其他回答，如果没有则更新问题状态为未回答
        List<AnswerVO> answers = answerMapper.selectByQuestionId(answer.getQuestionId(), null);
        if (answers.isEmpty()) {
            questionMapper.updateAnsweredStatus(answer.getQuestionId(), 0);
        }

        log.info("回答删除成功");
    }

    @Override
    public PageResult getMyAnswers(Integer pageNum, Integer pageSize, Long teacherId) {
        log.info("查询我的回答列表 - 教师ID: {}", teacherId);

        PageHelper.startPage(pageNum, pageSize);
        Page<AnswerVO> page = (Page<AnswerVO>) answerMapper.selectByTeacherId(teacherId);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void likeAnswer(Long answerId, Long userId) {
        log.info("点赞回答 - 回答ID: {}, 用户ID: {}", answerId, userId);

        Answer answer = answerMapper.selectById(answerId);
        if (answer == null) {
            throw new ServiceException("回答不存在");
        }

        // 检查是否已点赞
        AnswerLike existLike = answerLikeMapper.selectByAnswerAndUser(answerId, userId);
        if (existLike != null) {
            throw new ServiceException("已经点赞过此回答");
        }

        // 添加点赞
        AnswerLike answerLike = new AnswerLike();
        answerLike.setAnswerId(answerId);
        answerLike.setUserId(userId);
        answerLikeMapper.save(answerLike);

        // 增加点赞数
        answerMapper.increaseLikeCount(answerId);

        log.info("点赞成功");
    }

    @Override
    @Transactional
    public void unlikeAnswer(Long answerId, Long userId) {
        log.info("取消点赞 - 回答ID: {}, 用户ID: {}", answerId, userId);

        answerLikeMapper.delete(answerId, userId);
        answerMapper.decreaseLikeCount(answerId);

        log.info("取消点赞成功");
    }
}


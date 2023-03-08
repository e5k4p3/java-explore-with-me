package ru.practicum.comment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dao.CommentRepository;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.service.CommentService;
import ru.practicum.comment.util.CommentMapper;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.ForbiddenOperationException;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;
import ru.practicum.util.PageableMaker;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentResponseDto addComment(Long userId, Long eventId, CommentRequestDto commentRequestDto) {
        User commentator = checkUserExistence(userId);
        Event event = checkEventExistence(eventId);
        Comment comment = CommentMapper.toComment(commentRequestDto, event, commentator);
        log.info("Комментарий пользователя с id " + userId + " на событие с id " + eventId + " был добавлен.");

        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto) {
        checkUserExistence(userId);
        Comment comment = checkCommentExistence(commentId);
        checkCommentator(userId, comment.getCommentator().getId());
        comment.setText(commentRequestDto.getText());
        comment.setEdited(true);
        log.info("Комментарий с id " + commentId + " был изменен.");

        return CommentMapper.toCommentResponseDto(comment);
    }

    @Override
    @Transactional
    public void deleteCommentById(Long userId, Long commentId) {
        checkUserExistence(userId);
        Comment comment = checkCommentExistence(commentId);
        checkCommentator(userId, comment.getCommentator().getId());
        commentRepository.deleteById(commentId);
        log.info("Комментарий с id " + commentId + " был удален.");
    }

    @Override
    @Transactional
    public void deleteCommentByIdByAdmin(Long commentId) {
        checkCommentExistence(commentId);
        commentRepository.deleteById(commentId);
        log.info("Комментарий с id " + commentId + " был удален.");
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponseDto getCommentById(Long commentId) {
        Comment comment = checkCommentExistence(commentId);

        return CommentMapper.toCommentResponseDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getAllCommentsByEventId(Long eventId, Integer from, Integer size) {
        checkEventExistence(eventId);

        return commentRepository.findAllByEventId(eventId, PageableMaker.makePage(from, size)).stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getAllCommentsByCommentatorId(Long commentatorId, Integer from, Integer size) {
        checkUserExistence(commentatorId);

        return commentRepository.findAllByCommentatorId(commentatorId, PageableMaker.makePage(from, size)).stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getAllCommentsByCommentatorIdAndEventId(Long userId, Long eventId, Integer from, Integer size) {
        checkUserExistence(userId);
        checkEventExistence(eventId);

        return commentRepository.findAllByCommentatorIdAndEventId(userId, eventId, PageableMaker.makePage(from, size)).stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }

    private void checkCommentator(Long userId, Long commentatorId) {
        if (!userId.equals(commentatorId)) {
            throw new ForbiddenOperationException("Нельзя отредактировать чужой комментарий.");
        }
    }

    private Comment checkCommentExistence(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Комментарий с id " + commentId + " не найден."));
    }

    private User checkUserExistence(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId + " не найден."));
    }

    private Event checkEventExistence(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id " + eventId + " не найдено."));
    }
}

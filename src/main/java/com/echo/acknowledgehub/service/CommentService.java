package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository COMMENT_REPOSITORY;
    private final ModelMapper MODEL_MAPPER;

}

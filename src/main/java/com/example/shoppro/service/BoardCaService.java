package com.example.shoppro.service;

import com.example.shoppro.dto.BoardCaDTO;
import com.example.shoppro.dto.CategoryDTO;
import com.example.shoppro.entity.BoardCa;
import com.example.shoppro.entity.Category;
import com.example.shoppro.repository.BoardCaRepository;
import com.example.shoppro.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class BoardCaService {
    private final CategoryRepository categoryRepository;
    private final BoardCaRepository boardCaRepository;
    private final ModelMapper modelMapper;

    public void saveca(BoardCaDTO boardCaDTO){
        Category category =
                categoryRepository.findById(boardCaDTO.getCaid()).get();

        BoardCa boardCa = modelMapper.map(boardCaDTO, BoardCa.class);

        boardCa.setCategory(category);

        boardCaRepository.save(boardCa);
    }

    public List<BoardCaDTO> list(BoardCaDTO boardCaDTO){

        if (boardCaDTO.getCaid() == null){
            return boardCaRepository.findAll().stream().map(boardCa ->
                    modelMapper.map(boardCa, BoardCaDTO.class))
                    .collect(Collectors.toList());
        }else {
            return boardCaRepository.findByCategoryId(boardCaDTO.getCaid()).stream().map(boardCa ->
                    modelMapper.map(boardCa, BoardCaDTO.class))
                    .collect(Collectors.toList());
        }
    }
}

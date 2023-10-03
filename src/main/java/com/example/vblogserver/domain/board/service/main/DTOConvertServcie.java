package com.example.vblogserver.domain.board.service.main;

import com.example.vblogserver.domain.board.dto.MainBoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DTOConvertServcie {
    public List<MainBoardDTO> BoardToMainBoard(List<Board> boards){
        //조회 건수 60으로 제한
        int limit = 60;
        //Board DTO 를 MainBoardDTO 로 형변환
        List<MainBoardDTO> clientDataDTOs = boards.stream()
                .limit(limit)
                .map(this::convertToClientDataDTO)
                .collect(Collectors.toList());
        return clientDataDTOs;
    }

    private MainBoardDTO convertToClientDataDTO(Board board) {
        MainBoardDTO clientDataDTO = new MainBoardDTO();
        LocalDate ContentDate = board.getCreatedDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDateTime = ContentDate.format(formatter);
        clientDataDTO.setContentDate(formattedDateTime);
        clientDataDTO.setContentTitle(board.getTitle());
        clientDataDTO.setUserName(board.getWriter());
        clientDataDTO.setContent(board.getDescription());
        clientDataDTO.setHashtags(board.getHashtag());
        clientDataDTO.setContentId(board.getId());
        clientDataDTO.setImgurl(board.getThumbnails());
        clientDataDTO.setHeart(board.getLikeCount());
        if (board.getReviewCount() != null) {
            clientDataDTO.setReview(board.getReviewCount());
        }
        System.out.println("convertToClientDataDTO() : "+board.getId());


        return clientDataDTO;
    }
}

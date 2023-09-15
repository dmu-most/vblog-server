package com.example.vblogserver.domain.board.service.main;

import com.example.vblogserver.domain.board.dto.MainBoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DTOConvertServcie {
    public List<MainBoardDTO> BoardToMainBoard(List<Board> boards){

        int limit = 60;
        List<MainBoardDTO> clientDataDTOs = boards.stream()
                .limit(limit)
                .map(this::convertToClientDataDTO)
                .collect(Collectors.toList());
        return clientDataDTOs;
    }

    private MainBoardDTO convertToClientDataDTO(Board board) {
        MainBoardDTO clientDataDTO = new MainBoardDTO();
        clientDataDTO.setContentDate(board.getCreatedDate());
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

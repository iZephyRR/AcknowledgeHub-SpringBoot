package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.constant.AnnouncementStatus;
import com.echo.acknowledgehub.constant.ContentType;
import com.echo.acknowledgehub.dto.AnnouncementDraftDTO;
import com.echo.acknowledgehub.entity.AnnouncementDraft;
import com.echo.acknowledgehub.repository.DraftRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DraftService {
    private final DraftRepository DRAFT_REPOSITORY;
    private final ModelMapper MODEL_MAPPER;

    public AnnouncementDraft saveDraft (AnnouncementDraft announcementDraft) {
        return DRAFT_REPOSITORY.save(announcementDraft);
    }

    @Transactional
    public List<AnnouncementDraftDTO> getDrafts(Long id) {
        List<Object[]> objectList = DRAFT_REPOSITORY.getDrafts(id,AnnouncementStatus.EDITING);
        return mapToDTOList(objectList);
    }

    public AnnouncementDraft getById( Long id){
        return DRAFT_REPOSITORY.findById(id).orElseThrow(() ->
                new NoSuchElementException("Announcement does not found"));
    }

    public void deleteDraft (Long id) {
         DRAFT_REPOSITORY.deleteById(id);
    }

    public List<AnnouncementDraftDTO> mapToDTOList (List<Object[]> objList){
        return objList.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public AnnouncementDraftDTO mapToDTO(Object[] row) {
        AnnouncementDraftDTO dto = new AnnouncementDraftDTO();
        dto.setId((Long) row[0]);
        dto.setTitle((String) row[1]);
        dto.setFileUrl((String) row[2]);
        dto.setCategoryName((String) row[3]);
        dto.setFilename((String) row[4]);
        dto.setContentType((ContentType) row[5]);
        dto.setDraftAt((LocalDate) row[6]);
        dto.setCategoryId((Long) row[7]);
        return dto;
    }



}

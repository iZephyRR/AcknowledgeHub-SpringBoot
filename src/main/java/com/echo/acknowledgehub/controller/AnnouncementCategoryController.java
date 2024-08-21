package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.service.AnnouncementCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("${app.api.base-url}")
@AllArgsConstructor
public class AnnouncementCategoryController {

    private final AnnouncementCategoryService ANNOUNCEMENT_CATEGORY_SERVICE;

    @PostMapping("/mr/create-category")
    public CompletableFuture<ResponseEntity<AnnouncementCategory>> createCategory(@RequestBody AnnouncementCategory category) {
        return ANNOUNCEMENT_CATEGORY_SERVICE.save(category)
                .thenApply(savedCategory -> new ResponseEntity<>(savedCategory, HttpStatus.CREATED));
    }

    @GetMapping("/mr/get-categories")
    public ResponseEntity<List<AnnouncementCategory>> getAllCategories() {
        return ResponseEntity.ok(ANNOUNCEMENT_CATEGORY_SERVICE.findAll());

    }

    @PutMapping("/{id}/soft-delete")
    public CompletableFuture<ResponseEntity<Void>> softDeleteCategory(@PathVariable Long id) {
        return ANNOUNCEMENT_CATEGORY_SERVICE.softDelete(id)
                .thenApply(result -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}

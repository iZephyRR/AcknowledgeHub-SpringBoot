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
@RequestMapping("${app.api.base-url}/mr/category")
@AllArgsConstructor
public class AnnouncementCategoryController {

    private final AnnouncementCategoryService announcementCategoryService;

    @PostMapping("/create-category")
    public AnnouncementCategory createCategory(@RequestBody AnnouncementCategory category) {
        return announcementCategoryService.save(category).join();
    }

    @GetMapping("/mr/get-categories")
    public CompletableFuture<ResponseEntity<List<AnnouncementCategory>>> getAllCategories() {
        return announcementCategoryService.findAll()
                .thenApply(categories -> new ResponseEntity<>(categories, HttpStatus.OK));
    }

    @PutMapping("/{id}/soft-delete")
    public CompletableFuture<ResponseEntity<Void>> softDeleteCategory(@PathVariable Long id) {
        return announcementCategoryService.softDelete(id)
                .thenApply(result -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}

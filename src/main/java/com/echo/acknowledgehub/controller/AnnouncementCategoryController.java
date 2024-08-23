package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.service.AnnouncementCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
@RequestMapping("${app.api.base-url}/mr/category")
@AllArgsConstructor
public class AnnouncementCategoryController {

    private static final Logger LOGGER = Logger.getLogger(AnnouncementCategoryController.class.getName());

    private final AnnouncementCategoryService ANNOUNCEMENT_CATEGORY_SERVICE;

@PostMapping("/create")
public AnnouncementCategory createCategory(@RequestBody AnnouncementCategory category) {
    return ANNOUNCEMENT_CATEGORY_SERVICE.save(category).join();
}


    @GetMapping("/get-all")
    public ResponseEntity<List<AnnouncementCategory>> getAllCategories() {
        return ResponseEntity.ok(ANNOUNCEMENT_CATEGORY_SERVICE.getAllCategoriesDESC());
    }

    @PutMapping("/disable/{id}")
    public Integer softDeleteCategory(@PathVariable("id") Long id) {
    LOGGER.info("Id : "+id);
        return ANNOUNCEMENT_CATEGORY_SERVICE.softDelete(id).join();


    }
    @PutMapping("/enable/{id}")
    public Integer softUndeleteCategory(@PathVariable("id") Long id) {
      return  ANNOUNCEMENT_CATEGORY_SERVICE.softUndelete(id).join();


    }

}

package com.concord.catalogservice.controller;

import com.concord.catalogservice.dto.CopyRequest;
import com.concord.catalogservice.dto.UpdateStatusRequest;
import com.concord.catalogservice.entity.Copy;
import com.concord.catalogservice.entity.CopyStatus;
import com.concord.catalogservice.service.CopyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/copies")
public class CopyController {

    private final CopyService copyService;

    public CopyController(CopyService copyService) {
        this.copyService = copyService;
    }

    @GetMapping
    public List<Copy> getAllCopies() {
        return copyService.getAllCopies();
    }

    @GetMapping("/{id}")
    public Copy getCopyById(@PathVariable int id) {
        return copyService.getCopyById(id);
    }


    @PostMapping
    public Copy createCopy(@RequestBody CopyRequest copyRequest) {
        return copyService.createCopy(copyRequest.getBookId(), copyRequest.getCopyNumber(), copyRequest.getLocation());
    }

    @PatchMapping("/{copyId}/status")
    public Copy updateCopyStatus(@PathVariable int copyId, @RequestBody UpdateStatusRequest request) {
        return copyService.updateCopyStatus(copyId, CopyStatus.valueOf(request.getStatus().toUpperCase()));
    }
}

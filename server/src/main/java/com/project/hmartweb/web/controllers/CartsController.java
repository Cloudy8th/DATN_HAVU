package com.project.hmartweb.web.controllers;

import com.project.hmartweb.application.services.cart.ICartService;
import com.project.hmartweb.domain.dtos.CartDTO;
import com.project.hmartweb.domain.entities.Cart;
import com.project.hmartweb.web.base.RolesAdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/carts")
public class CartsController {
    @Autowired
    private ICartService cartService;

    @GetMapping("/user/{id}")
    @RolesAdminUser
    public ResponseEntity<?> getAllCarts(
            @PathVariable UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer perPage
    ) {
        var result = cartService.getAllByUser(id, page, perPage);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @RolesAdminUser
    public ResponseEntity<?> getCart(@PathVariable UUID id) {
        var result = cartService.getById(id);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @RolesAdminUser
    public ResponseEntity<?> deleteCart(@PathVariable UUID id) {
        Cart cart = cartService.getById(id);
        cartService.delete(cart);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PostMapping("")
    @RolesAdminUser
    public ResponseEntity<?> insertCart(@RequestBody CartDTO cartDTO) {
        var result = cartService.insert(cartDTO);
        return ResponseEntity.status(201).body(result);
    }

    @PutMapping("/{id}")
    @RolesAdminUser
    public ResponseEntity<?> updateCart(@PathVariable UUID id, @RequestBody CartDTO cartDTO) {
        var result = cartService.update(id, cartDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/multiple")
    @RolesAdminUser
    public ResponseEntity<?> insertCarts(@RequestBody List<CartDTO> cartDTOS) {
        var result = cartService.insertMultiple(cartDTOS);
        return ResponseEntity.status(201).body(result);
    }

    @DeleteMapping("/multiple")
    @RolesAdminUser
    public ResponseEntity<?> deleteCarts(@RequestBody List<UUID> ids) {
        var result = cartService.deleteMultiple(ids);
        return ResponseEntity.ok("Deleted successfully");
    }
}

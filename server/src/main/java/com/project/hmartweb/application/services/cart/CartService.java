package com.project.hmartweb.application.services.cart;

import com.project.hmartweb.application.repositories.CartRepository;
import com.project.hmartweb.application.services.product.IProductService;
import com.project.hmartweb.application.services.user.IUserService;
import com.project.hmartweb.config.exceptions.InvalidParamException;
import com.project.hmartweb.config.exceptions.NotFoundException;
import com.project.hmartweb.domain.dtos.CartDTO;
import com.project.hmartweb.domain.entities.Cart;
import com.project.hmartweb.domain.entities.Product;
import com.project.hmartweb.domain.entities.User;
import com.project.hmartweb.domain.paginate.BasePagination;
import com.project.hmartweb.domain.paginate.Pagination;
import com.project.hmartweb.domain.paginate.PaginationDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {
    private final CartRepository cartRepository;
    private final IUserService userService;
    private final IProductService productService;
    private final ModelMapper mapper;

    @Override
    public Cart insert(CartDTO cartDTO) {
        Optional<Cart> cartExists = cartRepository.findByProductId(cartDTO.getProductId());
        User user = userService.getById(cartDTO.getUserId());
        Product product = productService.getById(cartDTO.getProductId());

        if (cartExists.isPresent() && cartExists.get().getUser().getId().equals(cartDTO.getUserId())) {
            cartExists.get().setQuantity(cartExists.get().getQuantity() + cartDTO.getQuantity());
            if (product.getQuantity() < cartExists.get().getQuantity()) {
                throw new InvalidParamException("Số lượng sản phẩm trong kho không đủ", "Quantity not enough");
            }
            return cartRepository.save(cartExists.get());
        }
        if (product.getQuantity() < cartDTO.getQuantity()) {
            throw new InvalidParamException("Số lượng sản phẩm trong kho không đủ", "Quantity not enough");
        }
        Cart cart = mapper.map(cartDTO, Cart.class);
        cart.setUser(user);
        cart.setProduct(product);
        return cartRepository.save(cart);
    }

    @Override
    public Cart update(UUID id, CartDTO cartDTO) {
        Cart cart = getById(id);
        cart.setQuantity(cartDTO.getQuantity());
        return cartRepository.save(cart);
    }

    @Override
    public void delete(Cart cart) {
        cartRepository.delete(cart);
    }

    @Override
    public PaginationDTO<Cart> getAll(Integer page, Integer perPage) {
        if (page == null && perPage == null) {
            return new PaginationDTO<>(cartRepository.findAll(), null);
        }
        BasePagination<Cart, CartRepository> basePagination = new BasePagination<>(cartRepository);
        PaginationDTO<Cart> pageRequest = basePagination.paginate(page, perPage);
        return new PaginationDTO<>(pageRequest.getData(), pageRequest.getPagination());
    }

    @Override
    public Optional<Cart> findById(UUID id) {
        return cartRepository.findById(id);
    }

    @Override
    public Cart getById(UUID id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Giỏ hàng không tồn tại!", "Cart not found"));
    }

    @Override
    public PaginationDTO<Cart> getAllByUser(UUID userId, Integer page, Integer perPage) {
        if (page == null && perPage == null) {
            return new PaginationDTO<>(cartRepository.findAllByUser_Id(userId, Sort.by("updatedAt").descending()), null);
        }
        int currentPage = page != null ? page : 0;
        int currentPerPage = perPage != null ? perPage : 10;
        Page<Cart> pageRequest = cartRepository.findAllByUser_Id(
                userId,
                PageRequest.of(currentPage, currentPerPage, Sort.by("updatedAt").descending())
        );
        Pagination pagination = new Pagination(page, perPage, pageRequest.getTotalPages() - 1, pageRequest.getTotalElements());
        return new PaginationDTO<>(pageRequest.getContent(), pagination);
    }

    @Override
    public List<Cart> insertMultiple(List<CartDTO> cartDTOS) {
        List<Cart> carts = new ArrayList<>();
        for (CartDTO cartDTO : cartDTOS) {
            carts.add(insert(cartDTO));
        }
        return carts;
    }

    @Override
    public List<Cart> updateMultiple(List<CartDTO> cartDTOS) {
        return List.of();
    }

    @Override
    public int deleteMultiple(List<UUID> ids) {
        int count = 0;
        for (UUID id : ids) {
            Cart cart = getById(id);
            delete(cart);
            count++;
        }
        return count;
    }
}

package com.lab5.cart.api;

import com.lab5.cart.infrastructure.CartDao;
import com.lab5.cart.infrastructure.CartItemDao;
import com.lab5.cart.domain.Cart;
import com.lab5.cart.domain.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController {
    private final CartDao cartDao;
    private final CartItemDao cartItemDao;
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    public CartController(CartDao cartDao, CartItemDao cartItemDao) {
        this.cartDao = cartDao;
        this.cartItemDao = cartItemDao;
    }

    @GetMapping
    public ResponseEntity<List<Cart>> getAllCarts() {
        try {
            logger.info("Attempting to retrieve all carts");
            List<Cart> carts = cartDao.findAll();
            logger.info("Successfully retrieved {} carts", carts != null ? carts.size() : 0);
            return ResponseEntity.ok(carts);
        } catch (SQLException e) {
            logger.error("SQLException occurred while retrieving carts: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Cart> createCart(@RequestBody Cart cart) {
        try {
            logger.info("Attempting to create cart for customer: {}", cart.getCustomerId());
            cartDao.create(cart);
            logger.info("Successfully created cart with ID: {}", cart.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(cart);
        } catch (SQLException e) {
            logger.error("SQLException occurred while creating cart: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cart> getCart(@PathVariable int id) {
        try {
            logger.info("Attempting to retrieve cart with ID: {}", id);
            Cart cart = cartDao.findById(id);
            if (cart == null) {
                logger.info("Cart with ID {} not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            logger.info("Successfully retrieved cart with ID: {}", id);
            return ResponseEntity.ok(cart);
        } catch (SQLException e) {
            logger.error("SQLException occurred while retrieving cart {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItem> addItemToCart(@PathVariable int cartId, @RequestBody CartItem item) {
        try {
            logger.info("Attempting to add item to cart {}: productId={}, quantity={}", cartId, item.getProductId(), item.getQuantity());
            CartItem newItem = new CartItem(cartId, item.getProductId(), item.getQuantity());
            cartItemDao.create(newItem);
            logger.info("Successfully added item to cart {} with item ID: {}", cartId, newItem.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(newItem);
        } catch (SQLException e) {
            logger.error("SQLException occurred while adding item to cart {}: {}", cartId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{cartId}/items")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable int cartId) {
        try {
            logger.info("Attempting to retrieve items for cart: {}", cartId);
            List<CartItem> items = cartItemDao.findByCartId(cartId);
            logger.info("Successfully retrieved {} items for cart {}", items != null ? items.size() : 0, cartId);
            return ResponseEntity.ok(items);
        } catch (SQLException e) {
            logger.error("SQLException occurred while retrieving items for cart {}: {}", cartId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{cartId}/validate")
    public ResponseEntity<String> validateCart(@PathVariable int cartId) {
        try {
            logger.info("Validating cart: {}", cartId);
            Cart cart = cartDao.findById(cartId);
            if (cart == null) {
                logger.warn("Cart {} not found", cartId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found");
            }
            
            List<CartItem> items = cartItemDao.findByCartId(cartId);
            if (items == null || items.isEmpty()) {
                logger.warn("Cart {} is empty", cartId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cart is empty");
            }
            
            logger.info("Cart {} validated successfully with {} items", cartId, items.size());
            return ResponseEntity.ok("VALID");
        } catch (SQLException e) {
            logger.error("Error validating cart {}: {}", cartId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Validation failed");
        }
    }

    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<String> clearCart(@PathVariable int cartId) {
        try {
            logger.info("Clearing cart: {}", cartId);
            List<CartItem> items = cartItemDao.findByCartId(cartId);
            if (items != null) {
                for (CartItem item : items) {
                    cartItemDao.deleteById(item.getId());
                }
            }
            logger.info("Cart {} cleared successfully", cartId);
            return ResponseEntity.ok("CLEARED");
        } catch (SQLException e) {
            logger.error("Error clearing cart {}: {}", cartId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Clear failed");
        }
    }
} 
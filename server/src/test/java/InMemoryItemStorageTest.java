
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.InMemoryItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
class InMemoryItemStorageTest {

    private InMemoryItemStorage itemStorage;

    private User testUser1;
    private User testUser2;
    private Item testItem1;
    private Item testItem2;
    private Item testItem3;

    @BeforeEach
    void setUp() {
        itemStorage = new InMemoryItemStorage();


        testUser1 = User.builder()
                .id(1L)
                .name("User 1")
                .email("user1@email.com")
                .build();

        testUser2 = User.builder()
                .id(2L)
                .name("User 2")
                .email("user2@email.com")
                .build();

        testItem1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .owner(testUser1)
                .build();

        testItem2 = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .owner(testUser1)
                .build();

        testItem3 = Item.builder()
                .id(3L)
                .name("Item 3")
                .description("Description 3")
                .available(false) // недоступная вещь
                .owner(testUser2)
                .build();
    }

    @Test
    void create_WhenValidItem_ShouldAddItemToStorage() {
        Item newItem = Item.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .owner(testUser1)
                .build();

        Item createdItem = itemStorage.create(newItem);

        assertNotNull(createdItem);
        assertNotNull(createdItem.getId());
        assertEquals("New Item", createdItem.getName());
        assertEquals("New Description", createdItem.getDescription());
        assertTrue(createdItem.getAvailable());
        assertEquals(testUser1, createdItem.getOwner());

        Optional<Item> retrievedItem = itemStorage.getItemById(createdItem.getId());
        assertTrue(retrievedItem.isPresent());
        assertEquals(createdItem, retrievedItem.get());
    }

    @Test
    void create_WhenMultipleItems_ShouldGenerateUniqueIds() {
        Item item1 = Item.builder()
                .name("Item 1")
                .description("Desc 1")
                .available(true)
                .owner(testUser1)
                .build();

        Item item2 = Item.builder()
                .name("Item 2")
                .description("Desc 2")
                .available(true)
                .owner(testUser1)
                .build();

        Item created1 = itemStorage.create(item1);
        Item created2 = itemStorage.create(item2);

        assertNotNull(created1.getId());
        assertNotNull(created2.getId());
        assertNotEquals(created1.getId(), created2.getId());
        assertEquals(created1.getId() + 1, created2.getId()); // ID должны быть последовательными
    }

    @Test
    void getItemById_WhenItemExists_ShouldReturnItem() {
        Item createdItem = itemStorage.create(testItem1);
        Long itemId = createdItem.getId();

        Optional<Item> result = itemStorage.getItemById(itemId);

        assertTrue(result.isPresent());
        assertEquals(createdItem, result.get());
        assertEquals("Item 1", result.get().getName());
        assertEquals(testUser1, result.get().getOwner());
    }

    @Test
    void getItemById_WhenItemNotExists_ShouldReturnEmptyOptional() {
        Long nonExistentId = 999L;

        Optional<Item> result = itemStorage.getItemById(nonExistentId);

        assertFalse(result.isPresent());
    }

    @Test
    void getAllItemsByUser_WhenUserHasItems_ShouldReturnUserItems() {
        itemStorage.create(testItem1);
        itemStorage.create(testItem2);
        itemStorage.create(testItem3);

        Collection<Item> user1Items = itemStorage.getAllItemsByUser(testUser1.getId());
        Collection<Item> user2Items = itemStorage.getAllItemsByUser(testUser2.getId());

        assertEquals(2, user1Items.size());
        assertTrue(user1Items.stream().allMatch(item -> item.getOwner().getId() == testUser1.getId()));

        assertEquals(1, user2Items.size());
        assertTrue(user2Items.stream().allMatch(item -> item.getOwner().getId() == testUser2.getId()));
    }

    @Test
    void getAllItemsByUser_WhenUserHasNoItems_ShouldReturnEmptyCollection() {
        Long userWithoutItemsId = 999L;

        Collection<Item> result = itemStorage.getAllItemsByUser(userWithoutItemsId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void edit_WhenValidUpdate_ShouldUpdateItemFields() {
        Item originalItem = itemStorage.create(testItem1);
        Long itemId = originalItem.getId();

        Item updateData = Item.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .owner(testUser1)
                .build();

        Item updatedItem = itemStorage.edit(updateData, itemId);

        assertNotNull(updatedItem);
        assertEquals(itemId, updatedItem.getId());
        assertEquals("Updated Name", updatedItem.getName());
        assertEquals("Updated Description", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
        assertEquals(testUser1, updatedItem.getOwner());

        Optional<Item> retrievedItem = itemStorage.getItemById(itemId);
        assertTrue(retrievedItem.isPresent());
        assertEquals("Updated Name", retrievedItem.get().getName());
    }

    @Test
    void edit_WhenPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        Item originalItem = itemStorage.create(testItem1);
        Long itemId = originalItem.getId();

        Item updateData = Item.builder()
                .name("Only Name Updated")
                .description(null)
                .available(null)
                .owner(testUser2)
                .build();

        Item updatedItem = itemStorage.edit(updateData, itemId);

        assertNotNull(updatedItem);
        assertEquals("Only Name Updated", updatedItem.getName());
        assertEquals("Description 1", updatedItem.getDescription());
        assertTrue(updatedItem.getAvailable());
        assertEquals(testUser1, updatedItem.getOwner());
    }

    @Test
    void edit_WhenBlankFields_ShouldNotUpdate() {
        // Arrange
        Item originalItem = itemStorage.create(testItem1);
        Long itemId = originalItem.getId();

        Item updateData = Item.builder()
                .name("   ")
                .description("   ")
                .available(true)
                .build();

        Item updatedItem = itemStorage.edit(updateData, itemId);

        assertEquals("Item 1", updatedItem.getName());
        assertEquals("Description 1", updatedItem.getDescription());
        assertTrue(updatedItem.getAvailable());
    }

    @Test
    void delete_WhenItemExists_ShouldRemoveItem() {
        Item createdItem = itemStorage.create(testItem1);
        Long itemId = createdItem.getId();

        assertTrue(itemStorage.getItemById(itemId).isPresent());

        itemStorage.delete(itemId, testUser1.getId());

        assertFalse(itemStorage.getItemById(itemId).isPresent());
    }

    @Test
    void delete_WhenItemNotExists_ShouldDoNothing() {
        Long nonExistentId = 999L;

        assertDoesNotThrow(() -> itemStorage.delete(nonExistentId, testUser1.getId()));
    }

    @Test
    void search_WhenMatchingItemsExist_ShouldReturnAvailableItems() {
        itemStorage.create(testItem1);
        itemStorage.create(testItem2);
        itemStorage.create(testItem3);

        String searchText = "item";

        List<Item> result = itemStorage.search(searchText);

        assertEquals(2, result.size()); // только доступные вещи
        assertTrue(result.stream().allMatch(Item::getAvailable));
        assertTrue(result.stream().anyMatch(item -> item.getName().toLowerCase().contains("item")));
    }

    @Test
    void search_WhenTextInDescription_ShouldReturnMatchingItems() {
        itemStorage.create(testItem1);
        itemStorage.create(testItem2);

        String searchText = "description";

        List<Item> result = itemStorage.search(searchText);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(item ->
                item.getDescription().toLowerCase().contains("description")));
    }

    @Test
    void search_WhenNoMatches_ShouldReturnEmptyList() {
        itemStorage.create(testItem1);
        itemStorage.create(testItem2);

        String searchText = "nonexistent";

        List<Item> result = itemStorage.search(searchText);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void search_WhenEmptyText_ShouldReturnEmptyList() {
        itemStorage.create(testItem1);
        itemStorage.create(testItem2);

        String emptyText = "";

        List<Item> result = itemStorage.search(emptyText);

        assertNotNull(result);
    }

    @Test
    void search_WhenOnlyUnavailableItemsMatch_ShouldReturnEmptyList() {
        itemStorage.create(testItem3);

        String searchText = "item";

        List<Item> result = itemStorage.search(searchText);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void hasAccess_WhenUserIsOwner_ShouldNotThrowException() {
        Item createdItem = itemStorage.create(testItem1);
        Long itemId = createdItem.getId();

        assertDoesNotThrow(() -> itemStorage.hasAccess(itemId, testUser1.getId()));
    }

    @Test
    void hasAccess_WhenUserIsNotOwner_ShouldThrowForbiddenException() {
        Item createdItem = itemStorage.create(testItem1);
        Long itemId = createdItem.getId();
        Long differentUserId = testUser2.getId();

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> itemStorage.hasAccess(itemId, differentUserId));

        assertTrue(exception.getMessage().contains("отсутствие прав доступа"));
    }

    @Test
    void isItemExist_WhenItemExists_ShouldNotThrowException() {
        Item createdItem = itemStorage.create(testItem1);
        Long itemId = createdItem.getId();

        assertDoesNotThrow(() -> itemStorage.isItemExist(itemId));
    }

    @Test
    void isItemExist_WhenItemNotExists_ShouldThrowNotFoundException() {
        Long nonExistentId = 999L;

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemStorage.isItemExist(nonExistentId));

        assertTrue(exception.getMessage().contains("не найдена"));
    }

}
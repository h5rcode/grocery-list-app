package com.h5rcode.mygrocerylist.services;

import com.h5rcode.mygrocerylist.apiclient.GroceryListClient;
import com.h5rcode.mygrocerylist.apiclient.models.ApiResponse;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;
import com.h5rcode.mygrocerylist.services.models.GroceryItemUpdateResult;
import com.h5rcode.mygrocerylist.services.models.GroceryList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroceryListServiceImplTest {

    @Mock
    GroceryListClient groceryListClient;

    @Test
    public void deleteGroceryItem_should_call_groceryListClient_deleteGroceryItem() throws Exception {
        long id = new Random(1000).nextLong();
        GroceryListService groceryListService = new GroceryListServiceImpl(groceryListClient);

        groceryListService.deleteGroceryItem(id);

        verify(groceryListClient, times(1)).deleteGroceryItem(id);
    }

    @Test
    public void getGroceryList_should_return_an_instance_of_GroceryList_containing_the_items_and_categories_returned_by_groceryListClient() throws Exception {
        List<GroceryItemCategory> groceryItemCategories = new ArrayList<>();
        List<GroceryItem> groceryItems = new ArrayList<>();

        when(groceryListClient.getGroceryItemCategories()).thenReturn(groceryItemCategories);
        when(groceryListClient.getGroceryItems()).thenReturn(groceryItems);

        GroceryListService groceryListService = new GroceryListServiceImpl(groceryListClient);
        GroceryList groceryList = groceryListService.getGroceryList();

        assertEquals(groceryItemCategories, groceryList.getGroceryItemCategories());
        assertEquals(groceryItems, groceryList.getGroceryItems());
    }

    @Test
    public void updateGroceryItem_should_return_200_and_GroceryItem_returned_by_groceryApiClient_when_update_is_successful() throws Exception {
        GroceryItem groceryItemToUpdate = new GroceryItem();
        GroceryItem groceryItemUpdated = new GroceryItem();
        ApiResponse<GroceryItem> apiResponse = new ApiResponse<>(200, groceryItemUpdated);

        when(groceryListClient.updateGroceryItem(groceryItemToUpdate))
                .thenReturn(apiResponse);

        GroceryListService groceryListService = new GroceryListServiceImpl(groceryListClient);
        GroceryItemUpdateResult groceryItemUpdateResult = groceryListService.updateGroceryItem(groceryItemToUpdate);

        assertEquals(200, groceryItemUpdateResult.getResponseStatusCode());
        assertEquals(groceryItemUpdated, groceryItemUpdateResult.getGroceryItem());
    }

    @Test
    public void updateGroceryItem_should_return_412_and_null_when_the_item_has_been_updated_by_an_other_user() throws Exception {
        long id = new Random(1000).nextLong();
        GroceryItem groceryItemToUpdate = new GroceryItem();
        groceryItemToUpdate.id = id;

        GroceryItem mostRecentGroceryItem = new GroceryItem();

        ApiResponse<GroceryItem> apiResponse = new ApiResponse<>(412, null);

        when(groceryListClient.updateGroceryItem(groceryItemToUpdate))
                .thenReturn(apiResponse);

        when(groceryListClient.getItem(id))
                .thenReturn(mostRecentGroceryItem);

        GroceryListService groceryListService = new GroceryListServiceImpl(groceryListClient);
        GroceryItemUpdateResult groceryItemUpdateResult = groceryListService.updateGroceryItem(groceryItemToUpdate);

        assertEquals(412, groceryItemUpdateResult.getResponseStatusCode());
        assertEquals(mostRecentGroceryItem, groceryItemUpdateResult.getGroceryItem());
    }

    @Test
    public void updateGroceryItem_should_return_404_and_null_when_the_update_fails_and_the_item_to_update_is_not_found() throws Exception {
        long id = new Random(1000).nextLong();
        GroceryItem groceryItemToUpdate = new GroceryItem();
        groceryItemToUpdate.id = id;
        ApiResponse<GroceryItem> apiResponse = new ApiResponse<>(412, null);

        when(groceryListClient.updateGroceryItem(groceryItemToUpdate))
                .thenReturn(apiResponse);

        when(groceryListClient.getItem(id))
                .thenReturn(null);

        GroceryListService groceryListService = new GroceryListServiceImpl(groceryListClient);
        GroceryItemUpdateResult groceryItemUpdateResult = groceryListService.updateGroceryItem(groceryItemToUpdate);

        assertEquals(404, groceryItemUpdateResult.getResponseStatusCode());
        assertNull(groceryItemUpdateResult.getGroceryItem());
    }

    @Test(expected = RuntimeException.class)
    public void updateGroceryItem_should_throw_a_RuntimeException_when_the_update_returns_a_code_different_from_200_and_412() throws Exception {
        GroceryItem groceryItemToUpdate = new GroceryItem();
        ApiResponse<GroceryItem> apiResponse = new ApiResponse<>(500, null);

        when(groceryListClient.updateGroceryItem(groceryItemToUpdate))
                .thenReturn(apiResponse);


        GroceryListService groceryListService = new GroceryListServiceImpl(groceryListClient);
        groceryListService.updateGroceryItem(groceryItemToUpdate);
    }

    @Test
    public void addGroceryItem_should_call_groceryListClient_addGroceryItem() throws Exception {
        GroceryItem groceryItem = new GroceryItem();

        GroceryListService groceryListService = new GroceryListServiceImpl(groceryListClient);
        groceryListService.addGroceryItem(groceryItem);

        verify(groceryListClient, times(1)).addGroceryItem(groceryItem);
    }

    @Test
    public void getCategoryItemCategories_should_call_groceryListClient_getCategoryItemCategories() throws Exception {
        List<GroceryItemCategory> groceryItemCategories = new ArrayList<>();

        when(groceryListClient.getGroceryItemCategories())
                .thenReturn(groceryItemCategories);

        GroceryListService groceryListService = new GroceryListServiceImpl(groceryListClient);
        List<GroceryItemCategory> groceryItemCategoriesReturnedByService = groceryListService.getGroceryItemCategories();

        assertEquals(groceryItemCategories, groceryItemCategoriesReturnedByService);
    }

    @Test
    public void getGroceryItem_should_call_groceryListClient_getGroceryItem() throws Exception {
        GroceryItem groceryItem = new GroceryItem();

        long id = new Random(1000).nextLong();
        when(groceryListClient.getItem(id))
                .thenReturn(groceryItem);

        GroceryListService groceryListService = new GroceryListServiceImpl(groceryListClient);
        GroceryItem groceryItemReturnedByService = groceryListService.getGroceryItem(id);

        assertEquals(groceryItem, groceryItemReturnedByService);
    }
}

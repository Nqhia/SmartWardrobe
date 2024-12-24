package vn.edu.usth.smartwaro.mycloset;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ClosetViewModel extends ViewModel {
    private final MutableLiveData<List<ClothingItem>> allItems = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<ClothingItem>> getAllItems() {
        return allItems;
    }

    public void addItem(ClothingItem item) {
        List<ClothingItem> currentItems = allItems.getValue();
        if (currentItems != null) {
            currentItems.add(item);
            allItems.setValue(currentItems);
        }
    }
}
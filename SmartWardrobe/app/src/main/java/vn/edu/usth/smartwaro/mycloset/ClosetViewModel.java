// ClosetViewModel.java
package vn.edu.usth.smartwaro.mycloset;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ClosetViewModel extends ViewModel {
    private final MutableLiveData<List<ClothingItem>> upperBodyItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ClothingItem>> lowerBodyItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ClothingItem>> footwearItems = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<ClothingItem>> getUpperBodyItems() {
        return upperBodyItems;
    }

    public LiveData<List<ClothingItem>> getLowerBodyItems() {
        return lowerBodyItems;
    }

    public LiveData<List<ClothingItem>> getFootwearItems() {
        return footwearItems;
    }

    public void addUpperBodyItem(ClothingItem item) {
        List<ClothingItem> currentList = upperBodyItems.getValue();
        if (currentList != null) {
            currentList.add(item);
            upperBodyItems.setValue(currentList);
        }
    }

    public void addLowerBodyItem(ClothingItem item) {
        List<ClothingItem> currentList = lowerBodyItems.getValue();
        if (currentList != null) {
            currentList.add(item);
            lowerBodyItems.setValue(currentList);
        }
    }

    public void addFootwearItem(ClothingItem item) {
        List<ClothingItem> currentList = footwearItems.getValue();
        if (currentList != null) {
            currentList.add(item);
            footwearItems.setValue(currentList);
        }
    }
}

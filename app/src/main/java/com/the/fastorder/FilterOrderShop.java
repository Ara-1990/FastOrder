package com.the.fastorder;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterOrderShop extends Filter {

    private AdapterOrdeShop adapter;
    private ArrayList<ModelOrderShop> filterLists;

    public FilterOrderShop(AdapterOrdeShop adapter, ArrayList<ModelOrderShop> filterLists ){
        this.adapter = adapter;
        this.filterLists = filterLists;

    }
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length()>0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelOrderShop> fillteredModels = new ArrayList<>();
            for (int i=0; i<filterLists.size(); i++){
                if (filterLists.get(i).getOrderStatus().toUpperCase().contains(constraint) ){
                    fillteredModels.add(filterLists.get(i));
                }
            }
            results.count = fillteredModels.size();
            results.values = fillteredModels;
        }
        else {

            results.count = filterLists.size();
            results.values = filterLists;

        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.orderShopArrayList = (ArrayList<ModelOrderShop>) results.values;
        adapter.notifyDataSetChanged();

    }
}


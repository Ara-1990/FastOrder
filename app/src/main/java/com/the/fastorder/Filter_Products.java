package com.the.fastorder;

import android.widget.Filter;

import java.util.ArrayList;

public class Filter_Products extends Filter {

    private MenuView_Holder adapter;
    private ArrayList<Category> filterLists;

    public Filter_Products(MenuView_Holder adapter, ArrayList<Category> filterLists ){
        this.adapter = adapter;
        this.filterLists = filterLists;

    }
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length()>0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<Category> fillteredModels = new ArrayList<>();
            for (int i=0; i<filterLists.size(); i++){
                if (filterLists.get(i).getProductTitle().toUpperCase().contains(constraint) ||
                        filterLists.get(i).getProductCategory().toUpperCase().contains(constraint )){
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
        adapter.productList = (ArrayList<Category>) results.values;
        adapter.notifyDataSetChanged();

    }
}


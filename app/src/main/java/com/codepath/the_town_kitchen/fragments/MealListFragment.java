package com.codepath.the_town_kitchen.fragments;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.adapters.MealAdapter;
import com.codepath.the_town_kitchen.models.Meal;
import com.codepath.the_town_kitchen.models.Order;
import com.codepath.the_town_kitchen.models.OrderItem;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MealListFragment extends Fragment implements  MealAdapter.IActionClickListener {

    private ListView lvList;
    private MealAdapter mealAdapter;
    private ArrayList<com.codepath.the_town_kitchen.models.Meal> meals;
    private static String TAG = MealListFragment.class.getSimpleName();

    private String orderId;

    private Order orderToSave;
private  ICountUpdateListener countUpdateListener;

    public MealListFragment(ICountUpdateListener countUpdateListener) {
        // Required empty public constructor
        this.countUpdateListener = countUpdateListener;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "mealList fragment onCreatView");
        View view =  inflater.inflate(R.layout.fragment_meal_list, container, false);
        meals = new ArrayList<>();
        mealAdapter = new MealAdapter(getActivity(), meals, this);
        lvList = (ListView) view.findViewById(R.id.lvList);
        lvList.setAdapter(mealAdapter);
      return view;
    }

    private List<OrderItem> items = new ArrayList<>();


    private Meal.IMealsReceivedListener mealsReceived = new Meal.IMealsReceivedListener() {
        @Override
        public void handle(List<Meal> parseMeals) {
          
            meals.clear();
            Log.d(TAG,"get meal list" + parseMeals.size());
            if(orderToSave == null || items == null || items.size() == 0){
                for(Meal meal : parseMeals){
                    meal.quantityOrdered = 0;
                }
            }else {
                for (OrderItem item : items) {
                    int index = parseMeals.indexOf(item.getMeal());

                    if (index > -1) {
                        Log.d(TAG, "this meal has orders " + index);
                        Meal meal = parseMeals.get(index);
                        meal.quantityOrdered = item.getQuantity();
                    }
                }

            }
            meals.addAll(parseMeals);
            mealAdapter.notifyDataSetChanged();
        }
    };



    @Override
    public void onActionClicked(int position, final int count) {
        final Meal meal = meals.get(position);
        meal.quantityOrdered = count;
        if(orderToSave == null){
            Order.createNewOrder(new Order.IOrderReceivedListener() {
                @Override
                public void handle(Order order, List<OrderItem> orderItems) {
                    orderToSave = order;
                    updateOrder(meal, count);
                    countUpdateListener.handle(orderToSave.getQuantity());
                }
            });
        }

        else {
            updateOrder(meal, count);
            countUpdateListener.handle(orderToSave.getQuantity());
        }

    }

    public interface ICountUpdateListener{
        void handle(int count);
        
    }
    private void updateOrder(Meal meal, int count) {
        orderToSave.update(meal, count);
    }

    public void setTime(int hourOfDay, int minute) {
        if( orderToSave != null) {
            orderToSave.setTime(hourOfDay + ":" + minute);

            orderToSave.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    orderId = orderToSave.getObjectId();
                }
            });
        }

    }

    public void getMeals(Order order) {
        orderToSave = order;
        items = order.getOrderItems();
        Meal.fromParse(mealsReceived);
    }
}

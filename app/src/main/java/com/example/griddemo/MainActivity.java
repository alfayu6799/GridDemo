package com.example.griddemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Service;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();  //debug

    private List<Function> functions = new ArrayList<>();
    private IconAdapter adapter;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFunction();

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new IconAdapter();
        recyclerView.setAdapter(adapter);
        helper.attachToRecyclerView(recyclerView);
    }

    private void setupFunction() {
        //從緩存區取出變更過的順序
        ArrayList<Function> items = (ArrayList<Function>) ACache.get(MainActivity.this).getAsObject("items");
        if (items!=null) {

            JSONArray array = new JSONArray();
            for(int i=0; i < items.size(); i++){
                JSONObject object = new JSONObject();
                try {
                    object.put("sort" , items.get(i).getName());
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "setupFunction : " + array.toString());
            functions.addAll(items);

        }else {
//        functions = new ArrayList<>();
            String[] funcs = getResources().getStringArray(R.array.functions);
            functions.add(new Function(funcs[0], R.drawable.ic_favorite_black_24dp));
            functions.add(new Function(funcs[1], R.drawable.ic_accessible_black_24dp));
            functions.add(new Function(funcs[2], R.drawable.ic_airline_seat_flat_angled_black_24dp));
            functions.add(new Function(funcs[3], R.drawable.ic_favorite_black_24dp));
            functions.add(new Function(funcs[4], R.drawable.ic_accessible_black_24dp));
            functions.add(new Function(funcs[5], R.drawable.ic_airline_seat_flat_angled_black_24dp));
            functions.add(new Function(funcs[6], R.drawable.ic_favorite_black_24dp));
            functions.add(new Function(funcs[7], R.drawable.ic_accessible_black_24dp));
            functions.add(new Function(funcs[8], R.drawable.ic_airline_seat_flat_angled_black_24dp));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ACache.get(MainActivity.this).put("items", (Serializable) functions); //將變更過的順序緩存
        SharedPreferences.Editor editor = getSharedPreferences("SORT_ORDER", MODE_PRIVATE).edit();

    }

    public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconHolder> {
        @NonNull
        @Override
        public IconHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_icon, parent, false);
            return new IconHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull IconHolder holder, int position) {
            Function function = functions.get(position);
            holder.nameText.setText(function.getName());
            holder.iconImage.setImageResource(function.getIcon());
        }

        @Override
        public int getItemCount() {
            return functions.size();
        }

        public class IconHolder extends RecyclerView.ViewHolder {
            ImageView iconImage;
            TextView nameText;

            public IconHolder(@NonNull View itemView) {
                super(itemView);
                iconImage = itemView.findViewById(R.id.item_icon);
                nameText = itemView.findViewById(R.id.item_name);
            }
        }
    }

    ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFrlg = 0;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager){
                dragFrlg = ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
            }else if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
                dragFrlg = ItemTouchHelper.UP|ItemTouchHelper.DOWN;
            }
            return makeMovementFlags(dragFrlg,0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();  //得到拖動ViewHolder的position
            int toPosition = target.getAdapterPosition();        //得到目標ViewHolder的position

            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(functions, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(functions, i, i - 1);
                }
            }
            adapter.notifyItemMoved(fromPosition, toPosition); //加入拖拉時其他項目的效果與資料更新
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }


        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        //當長按選中item的時候（拖拽開始的時候）調用
        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                viewHolder.itemView.setBackgroundColor(Color.RED);

                Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                vib.vibrate(70);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        //當手指松開的時候（拖拽完畢的時候）調用
        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundColor(0);
            adapter.notifyDataSetChanged();
        }
    });
}

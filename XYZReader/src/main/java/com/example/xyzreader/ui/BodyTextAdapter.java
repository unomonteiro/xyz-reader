package com.example.xyzreader.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;

import java.util.ArrayList;

class BodyTextAdapter extends RecyclerView.Adapter<BodyTextAdapter.BodyTextViewHolder> {

    private ArrayList<String> mBodyTextArray;

    public BodyTextAdapter(ArrayList<String> bodyTextArray) {
        mBodyTextArray = bodyTextArray;
    }

    @NonNull
    @Override
    public BodyTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_body, parent, false);
        return new BodyTextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BodyTextViewHolder holder, int position) {
        holder.paragraphView.setText(mBodyTextArray.get(position));
    }

    @Override
    public int getItemCount() {
        if (mBodyTextArray != null) {
            return mBodyTextArray.size();
        } else {
            return 0;
        }
    }

    class BodyTextViewHolder extends RecyclerView.ViewHolder {
        TextView paragraphView;
        BodyTextViewHolder(View itemView) {
            super(itemView);
            paragraphView = itemView.findViewById(R.id.article_body);
        }
    }
}

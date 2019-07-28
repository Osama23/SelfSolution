package com.osama.osama.whatsfirebase;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VerticalSpacingItemDecorator extends RecyclerView.ItemDecoration
{
    // denote the space bettween list items
    private final int verticalSpaceHeight;

    public VerticalSpacingItemDecorator(int verticalSpaceHeight)
    {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    // the space is donating by the rect object
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

        outRect.bottom = verticalSpaceHeight;

    }
}

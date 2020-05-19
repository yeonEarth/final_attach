package Page3_1_1_1;

import android.graphics.Canvas;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.helper.ItemTouchHelper;

public class TrainItemTouchHelper extends ItemTouchHelper.Callback {

    private ItemTouchHelperAdapter adapter;
    public TrainItemTouchHelper(ItemTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 헤더 뷰가 움직일 때
        if (viewHolder instanceof Page3_1_1_1_trainAdapter.HeaderViewHolder) {
            // 드래그 방향 설정
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = 0; // 스와이프 사용X
            return makeMovementFlags(dragFlags, swipeFlags);
        }
        return 0;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // 어느 위치에서 어느 위치로 이동할 것인지 설정
        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            float alpha = 1 - (Math.abs(dX) / recyclerView.getWidth());
            viewHolder.itemView.setAlpha(alpha);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    // 인터페이스 정의
    // 아이템 변경 시 from to 바꾸기
    public interface ItemTouchHelperAdapter {
        void onItemMove(int fromPos, int toPos);
    }
}

package co.quarkworks.sectionedrecyclerviewadapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jacobmuchow@quarkworks.co (Jacob Muchow)
 */

public abstract class SectionedRecyclerViewAdapter<VH extends RecyclerView.ViewHolder, A extends RecyclerView.Adapter<VH>> extends RecyclerView.Adapter<VH> {
    private static final String TAG = SectionedRecyclerViewAdapter.class.getSimpleName();

    public static final int NONE = -1;

    @NonNull
    private final List<A> adapters = new ArrayList<>();

    public SectionedRecyclerViewAdapter(@Nullable List<A> adapters) {
        if (adapters != null && !adapters.isEmpty()) {
            this.adapters.addAll(adapters);
        }
    }


    @Override
    public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType < 0 || viewType >= adapters.size()) {
            throw new IllegalStateException("viewType provided to onCreateViewHolder() must be an index for an adapter.");
        }
        return adapters.get(viewType).onCreateViewHolder(parent, 0);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        int posInAdapter = getPositionInAdapter(position);
        if (posInAdapter == NONE) {
            throw new IllegalStateException("Failed to bind. No position found in any sub-adapter for this position: pos=" + posInAdapter);
        }
        adapters.get(holder.getItemViewType()).onBindViewHolder(holder, posInAdapter);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (RecyclerView.Adapter adapter : adapters) {
            count += adapter.getItemCount();
        }
        return count;
    }

    @NonNull
    public List<A> getAdapters() {
        return adapters;
    }


    /**
     * Gets the position within an adapter for any RecyclerView item position.
     *
     * @return - position in adapter or -1 if not found.
     */
    public int getPositionInAdapter(int position) {
        if (position < 0 || position >= getItemCount()) {
            return NONE;
        }

        for (int i = 0; i < adapters.size(); i++) {
            A adapter = adapters.get(i);
            int itemCount = adapter.getItemCount();

            if (position < itemCount) {
                return position;
            }
            position -= itemCount;
        }

        return NONE;
    }

    /**
     * Gets the index of the adapter in the adapters list for any RecyclerView item position.
     *
     * @return - index of adapter or -1 if not found.
     */
    public int getAdapterPosForItemPos(int position) {
        if (position < 0 || position >= getItemCount()) {
            return NONE;
        }

        for (int i = 0; i < adapters.size(); i++) {
            A adapter = adapters.get(i);
            int itemCount = adapter.getItemCount();

            if (position < itemCount) {
                return i;
            }
            position -= itemCount;
        }

        return NONE;
    }

    /**
     * Gets the adapter in the adapters list for any RecyclerView item position.
     *
     * @return - an adapter or null if not found.
     */
    @Nullable
    public A getAdapterForItemPos(int position) {
        int index = getAdapterPosForItemPos(position);
        if (index < 0 || index >= adapters.size()) {
            return null;
        }
        return adapters.get(index);
    }

    /**
     * Using the adapter position in the list as the item view type lets us determine which
     * adapter to forward to in onCreateViewHolder().
     */
    @Override
    public final int getItemViewType(int position) {
        return getAdapterPosForItemPos(position);
    }
}

package com.seagazer.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Looper banner view extends {@link RecyclerView},
 * use {@link RecyclerView.Adapter} to provide page view and data.
 */
public class BannerViewPager extends RecyclerView {
    private static final int FIRST_POSITION = 1;
    private int touchSlop;
    private int downX, downY;
    private int currentPosition;
    private int delay = 5000;
    private BannerAdapterWrapper adapter;
    private LinearLayoutManager layoutManager;
    private boolean autoFlip = false;
    private boolean lazySetListener = false;
    private OnPageClickListener onPageClickListener;

    public BannerViewPager(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BannerViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerViewPager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        layoutManager = new LinearLayoutManager(context, HORIZONTAL, false);
        setLayoutManager(layoutManager);
        setHasFixedSize(true);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(this);
        addOnScrollListener(scrollListener);
    }

    private OnScrollListener scrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                int firstPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                currentPosition = firstPosition;
                if (firstPosition == 0) {
                    final int outerAdapterLastPosition = adapter.getItemCount() - 2;
                    scrollToPosition(outerAdapterLastPosition);
                    currentPosition = outerAdapterLastPosition;
                } else if (firstPosition == adapter.getItemCount() - 1) {
                    scrollToPosition(FIRST_POSITION);
                    currentPosition = FIRST_POSITION;
                }
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    /**
     * Set a click listener for current page.
     *
     * @param onPageClickListener ClickListener.
     */
    public void setOnPageClickListener(OnPageClickListener onPageClickListener) {
        if (this.adapter != null) {
            this.adapter.setOnPageClickListener(onPageClickListener);
        } else {
            lazySetListener = true;
            this.onPageClickListener = onPageClickListener;
        }
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        this.adapter = new BannerAdapterWrapper(adapter);
        super.setAdapter(this.adapter);
        scrollToPosition(FIRST_POSITION);
        currentPosition = FIRST_POSITION;
        if (lazySetListener) {
            this.adapter.setOnPageClickListener(this.onPageClickListener);
        }
    }

    /**
     * Scroll to a certain position.
     *
     * @param position     Scroll to this adapter position.
     * @param smoothScroll Use smooth scroller.
     */
    public void setCurrentPosition(int position, boolean smoothScroll) {
        int innerPosition = position + 1;
        if (smoothScroll) {
            smoothScrollToPosition(innerPosition);
        } else {
            scrollToPosition(innerPosition);
        }
    }

    /**
     * Get the index of current page.
     *
     * @return Index of current page.
     */
    public int getCurrentPosition() {
        return currentPosition - 1;
    }

    /**
     * Set auto flip to next page.
     *
     * @param autoFlip Auto flip to next.
     */
    public void setAutoFlip(boolean autoFlip) {
        this.autoFlip = autoFlip;
    }

    /**
     * Set auto flip time.
     *
     * @param duration The duration of interval flip action (millisecond).
     */
    public void setAutoFlipTime(int duration) {
        this.delay = duration;
    }

    private Runnable autoFlipTask = new Runnable() {
        @Override
        public void run() {
            if (autoFlip) {
                currentPosition++;
                int next = currentPosition % adapter.getItemCount();
                smoothScrollToPosition(next);
                postDelayed(this, delay);
                if (currentPosition == adapter.getItemCount() - 1) {
                    currentPosition = FIRST_POSITION;
                }
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Object tag = getTag(R.id.banner_position);
        if (tag != null) {
            scrollToPosition((Integer) tag);
            currentPosition = (int) tag;
        } else {
            scrollToPosition(currentPosition);
        }
        if (this.adapter != null && autoFlip) {
            resumeAutoFlip();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setTag(R.id.banner_position, currentPosition);
        if (autoFlip) {
            pauseAutoFlip();
        }
    }

    /**
     * Stop auto flip.
     */
    public void pauseAutoFlip() {
        removeCallbacks(autoFlipTask);
    }

    /**
     * Start auto flip.
     */
    public void resumeAutoFlip() {
        pauseAutoFlip();
        postDelayed(autoFlipTask, delay);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            downX = (int) e.getX();
            downY = (int) e.getY();
        } else if (action == MotionEvent.ACTION_MOVE) {
            float curX = e.getX();
            float curY = e.getY();
            float dx = Math.abs(curX - downX);
            float dy = Math.abs(curY - downY);
            if (dx > dy && dx > touchSlop) {
                pauseAutoFlip();
            }
            downX = (int) curX;
            downY = (int) curY;
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            resumeAutoFlip();
        }
        return super.onTouchEvent(e);
    }

    /**
     * Click listener of page.
     */
    public interface OnPageClickListener {
        /**
         * @param view     The current page view.
         * @param position The position of current page.
         */
        void onPageClick(View view, int position);
    }

    /**
     * Adapter wrapper to handle looper.
     */
    final class BannerAdapterWrapper extends RecyclerView.Adapter {
        private RecyclerView.Adapter outerAdapter;
        private OnPageClickListener onPageClickListener;

        BannerAdapterWrapper(RecyclerView.Adapter adapter) {
            outerAdapter = adapter;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final ViewHolder viewHolder = outerAdapter.onCreateViewHolder(parent, viewType);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPageClickListener != null) {
                        int realPosition = viewHolder.getAdapterPosition() - 1;
                        onPageClickListener.onPageClick(v, realPosition);
                    }
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int realPosition;
            if (position == 0) {// first, jump to the last of outerAdapter
                realPosition = outerAdapter.getItemCount() - 1;
            } else if (position == getItemCount() - 1) {// last, jump to the first of outerAdapter
                realPosition = 0;
            } else {
                realPosition = position - 1;
            }
            // this holder is same as holder of outAdapter
            outerAdapter.onBindViewHolder(holder, realPosition);
        }

        @Override
        public int getItemCount() {
            // add first and last page
            return outerAdapter.getItemCount() + 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (outerAdapter == null || outerAdapter.getItemCount() == 0) {
                return super.getItemViewType(position);
            } else {
                int realPosition;
                if (position == 0) {
                    realPosition = outerAdapter.getItemCount() - 1;
                } else if (position == getItemCount() - 1) {
                    realPosition = 0;
                } else {
                    realPosition = position - 1;
                }
                return outerAdapter.getItemViewType(realPosition);
            }
        }

        void setOnPageClickListener(OnPageClickListener onPageClickListener) {
            this.onPageClickListener = onPageClickListener;
        }
    }

}

package me.kcaldwell.hackernewsreader.adapters;

import android.app.ActionBar;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import me.kcaldwell.hackernewsreader.R;
import me.kcaldwell.hackernewsreader.data.Comment;
import me.kcaldwell.hackernewsreader.ui.CommentListFragment.OnCommentInteractionListener;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Comment} and makes a call to the
 * specified {@link OnCommentInteractionListener}.
 */
public class CommentRecyclerViewAdapter extends RealmRecyclerViewAdapter<Comment, CommentRecyclerViewAdapter.ViewHolder> {

    private final OnCommentInteractionListener mListener;

    public CommentRecyclerViewAdapter(OrderedRealmCollection<Comment> data, OnCommentInteractionListener listener) {
        super(data, true);
        mListener = listener;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Comment comment = getItem(position);
        holder.mComment = comment;
        indentView(holder.mView, comment.getLevel());
        if (comment.getLevel() > 0) {
            holder.mLevelUpIcon.setVisibility(View.VISIBLE);
        }
        else {
            holder.mLevelUpIcon.setVisibility(View.GONE);
        }

        holder.mAuthorTextView.setText(comment.getAuthor());
        holder.mContentView.setText(Html.fromHtml(comment.getContent()));
        holder.mTimeTextView.setText(comment.getTimeAgo());
        int commentsCount = comment.getCommentsCount();
        String commentsString;
        if (commentsCount == 0) {
            commentsString = "No replies";
        } else if (commentsCount == 1) {
            commentsString = "1 reply";
        } else {
            commentsString = commentsCount + " replies";
        }
        holder.mRepliesTextView.setText(commentsString);

        holder.mView.setOnClickListener(v -> {
            // TODO: Update to collapse child comments.
        });

        // Linkify any links shared in the article
        BetterLinkMovementMethod.linkify(Linkify.ALL, holder.mContentView).setOnLinkClickListener((textView, url) -> {
            mListener.onCommentUrlClicked(url);
            return true;
        });

        if (position == getItemCount() - 1) holder.mDivider.setVisibility(View.INVISIBLE);
    }

    private void indentView(View view, int level) {
        // Determine margin to indent by
        int marginLeft = 40 * level;
        int marginTop = level > 0 ? 0 : 40;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(marginLeft, marginTop, 0, 0);
        view.setLayoutParams(layoutParams);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mLevelUpIcon;
        final TextView mAuthorTextView;
        final TextView mContentView;
        final TextView mTimeTextView;
        final TextView mRepliesTextView;
        final View mDivider;
        Comment mComment;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mLevelUpIcon = view.findViewById(R.id.level_up_icon);
            mAuthorTextView = view.findViewById(R.id.author_text_view);
            mContentView = view.findViewById(R.id.content_text_view);
            mTimeTextView = view.findViewById(R.id.time_text_view);
            mRepliesTextView = view.findViewById(R.id.reply_count_text_view);
            mDivider = view.findViewById(R.id.comment_divider);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}

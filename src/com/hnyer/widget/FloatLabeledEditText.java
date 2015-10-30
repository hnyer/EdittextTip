package com.hnyer.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hnyer.edittexttip.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.animation.AnimatorProxy;

public class FloatLabeledEditText extends FrameLayout {

	//默认距离左端为0个距离
	private static   int DEFAULT_PADDING_LEFT = 0;
	//显示在输入框上端的提示文字
	private TextView mHintTextView;
	private EditText mEditText;
	private Context mContext;

	//构造方法
	public FloatLabeledEditText(Context context) {
		super(context);
		mContext = context;
	}

	//通过xml布局，必须要用带有AttributeSet这个参数的 构造方法
	public FloatLabeledEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context; 
		setAttributes(attrs);
	}

	// 构造方法
	public FloatLabeledEditText(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		setAttributes(attrs);
	}

	@SuppressWarnings("deprecation")
	private void setAttributes(AttributeSet attrs) {
		mHintTextView = new TextView(mContext); 
		//使用TypedArray简化获得属性value的步骤
		 TypedArray typedArray = mContext.obtainStyledAttributes(attrs,
				R.styleable.FloatLabeledEditText);

		 //获取xml设置的间距值，如果没有设置，默认为 0 
		  int padding = typedArray.getDimensionPixelSize(
				R.styleable.FloatLabeledEditText_fletPadding, 0);
		  
		  //采用COMPLEX_UNIT_DIP （dp）为单位，值为 DEFAULT_PADDING_LEFT  
		  int defaultPadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PADDING_LEFT,
				getResources().getDisplayMetrics());
		  
		  int paddingLeft = typedArray.getDimensionPixelSize(
				R.styleable.FloatLabeledEditText_fletPaddingLeft,
				defaultPadding);
		  int paddingTop = typedArray.getDimensionPixelSize(
				R.styleable.FloatLabeledEditText_fletPaddingTop, 0);
		  
		  int paddingRight = typedArray.getDimensionPixelSize(
				R.styleable.FloatLabeledEditText_fletPaddingRight, 0);
		  
		  int paddingBottom = typedArray.getDimensionPixelSize(
				R.styleable.FloatLabeledEditText_fletPaddingBottom, 0);
		  
		  //获取设置的背景
		  Drawable background = typedArray.getDrawable(R.styleable.FloatLabeledEditText_fletBackground);

		if (padding != 0) {
			mHintTextView.setPadding(padding, padding, padding, padding);
		} else {
			mHintTextView.setPadding(paddingLeft, paddingTop, paddingRight,
					paddingBottom);
		}

		if (background != null) {
			setHintBackground(background);
		}
 
		
		
        
		if (Build.VERSION.SDK_INT >= 23) {
			// This method was deprecated in API level 23. Use
			// setTextAppearance(int) instead.
			mHintTextView.setTextAppearance(typedArray.getResourceId(
					R.styleable.FloatLabeledEditText_fletTextAppearance,
					android.R.style.TextAppearance_Small)) ;
		}else{
			mHintTextView.setTextAppearance(mContext, typedArray.getResourceId(
					R.styleable.FloatLabeledEditText_fletTextAppearance,
					android.R.style.TextAppearance_Small));
		}
		
		// Start hidden
		mHintTextView.setVisibility(INVISIBLE);
		//利用nineoldandroids.jar 实现的动画
		AnimatorProxy.wrap(mHintTextView).setAlpha(0);
		
		addView(mHintTextView, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);

		typedArray.recycle();
	}

	//给提示文本框设置背景
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void setHintBackground(Drawable background) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mHintTextView.setBackground(background);
		} else {
			mHintTextView.setBackgroundDrawable(background);
		}
	}

	@Override
	public   void addView(View child, int index,
			ViewGroup.LayoutParams params) {
		if (child instanceof EditText) {
			if (mEditText != null) {
				throw new IllegalArgumentException("子控件只能为EditText");
			}

			LayoutParams lp = new LayoutParams(params);
			lp.gravity = Gravity.BOTTOM;
			lp.topMargin = (int) (mHintTextView.getTextSize()
					+ mHintTextView.getPaddingBottom() + mHintTextView
					.getPaddingTop());
			params = lp;

			setEditText((EditText) child); 
		}

		super.addView(child, index, params);
	}

	private void setEditText(EditText editText) {
		mEditText = editText;

		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				setShowHint(!TextUtils.isEmpty(s));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

		});

		mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean gotFocus) {
				onFocusChanged(gotFocus);
			}
		});

		mHintTextView.setText(mEditText.getHint());

		if (!TextUtils.isEmpty(mEditText.getText())) {
			mHintTextView.setVisibility(VISIBLE);
		}
	}

	private void onFocusChanged(boolean gotFocus) {  
		if (gotFocus && mHintTextView.getVisibility() == VISIBLE) {
			ObjectAnimator.ofFloat(mHintTextView, "alpha", 0.33f, 1f).start();
		} else if (mHintTextView.getVisibility() == VISIBLE) {
			AnimatorProxy.wrap(mHintTextView).setAlpha(1f); // Need this for
															// compat reasons
			ObjectAnimator.ofFloat(mHintTextView, "alpha", 1f, 0.33f).start();
		}
	}

	private void setShowHint(  final boolean show) {
		AnimatorSet animation = null;
		if ((mHintTextView.getVisibility() == VISIBLE) && !show) {
			animation = new AnimatorSet();
			ObjectAnimator move = ObjectAnimator.ofFloat(mHintTextView,
					"translationY", 0, mHintTextView.getHeight() / 8);
			ObjectAnimator fade = ObjectAnimator.ofFloat(mHintTextView,
					"alpha", 1, 0);
			animation.playTogether(move, fade);
		} else if ((mHintTextView.getVisibility() != VISIBLE) && show) {
			animation = new AnimatorSet();
			ObjectAnimator move = ObjectAnimator.ofFloat(mHintTextView,
					"translationY", mHintTextView.getHeight() / 8, 0);
			ObjectAnimator fade;
			if (mEditText.isFocused()) {
				fade = ObjectAnimator.ofFloat(mHintTextView, "alpha", 0, 1);
			} else {
				fade = ObjectAnimator.ofFloat(mHintTextView, "alpha", 0, 0.33f);
			}
			animation.playTogether(move, fade);
		}

		if (animation != null) {
			animation.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					super.onAnimationStart(animation);
					mHintTextView.setVisibility(VISIBLE);
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					mHintTextView.setVisibility(show ? VISIBLE : INVISIBLE);
					AnimatorProxy.wrap(mHintTextView).setAlpha(show ? 1 : 0);
				}
			});
			animation.start();
		}
	}

	public EditText getEditText() {
		return mEditText;
	}

	public void setHint(String hint) {
		mEditText.setHint(hint);
		mHintTextView.setText(hint);
	}

	public CharSequence getHint() {
		return mHintTextView.getHint();
	}

}

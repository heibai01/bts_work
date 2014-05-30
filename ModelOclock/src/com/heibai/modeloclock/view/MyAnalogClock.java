package com.heibai.modeloclock.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

import com.heibai.modeloclock.R;

/**
 * 自定义时钟控件
 * 
 * @author heibai
 * @company http://www.bts-led.com/
 * @date 2014年5月29日
 */
@SuppressLint("NewApi")
public class MyAnalogClock extends View {

	private Time mCalendar;
	/** 表盘,时针,分针,秒针 图片 */
	private Drawable mHourHand;
	private Drawable mMinuteHand;
	private Drawable mSecondHand;
	private Drawable mDial;

	private float mHour;// 时针值
	private float mMinutes;// 分针值
	private float mSecond;// 秒针值
	private boolean mChanged;// 是否需要更新界面

	/* 日期, 星期 */
	private String mDay;
	private String mWeek;

	/* 表盘宽高 */
	private int mDialWidth;
	private int mDialHeight;

	private final Handler mHandler = new Handler();

	private Paint mPaint;// 画笔

	private Runnable mTicker;// 由于秒针的存在，因此我们需要每秒钟都刷新一次界面，用的就是此任务

	private boolean mTickerStopped = false;// 是否停止更新时间，当View从窗口中分离时，不需要更新时间了

	public MyAnalogClock(Context context) {
		this(context, null);
	}

	public MyAnalogClock(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyAnalogClock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Resources r = getContext().getResources();
		// 下面是从layout文件中读取所使用的图片资源，如果没有则使用默认的
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.MyAnalogClock, defStyle, 0);
		mDial = a.getDrawable(R.styleable.MyAnalogClock_dial);
		mHourHand = a.getDrawable(R.styleable.MyAnalogClock_hand_hour);
		mMinuteHand = a.getDrawable(R.styleable.MyAnalogClock_hand_minute);
		mSecondHand = a.getDrawable(R.styleable.MyAnalogClock_hand_second);

		// 为了整体美观性，只要缺少一张图片，我们就用默认的程序绘制
		// if (mDial == null || mHourHand == null || mMinuteHand == null
		// || mSecondHand == null) {
		// mDial = r.getDrawable(R.drawable.default_dail);
		// mHourHand = r.getDrawable(R.drawable.default_clock_hour);
		// mMinuteHand = r.getDrawable(R.drawable.default_clock_min);
		// mSecondHand = r.getDrawable(R.drawable.default_clock_second);
		// }
		a.recycle();// 不调用这个函数，则上面的都是白费功夫

		init();
	}

	private void init() {
		// 初始化画笔
		mPaint = new Paint();
		mPaint.setColor(Color.parseColor("#3399ff"));
		mPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mPaint.setFakeBoldText(true);
		mPaint.setAntiAlias(true);

		// 初始化Time对象
		if (mCalendar == null) {
			mCalendar = new Time();
		}
	}

	/**
	 * 时间改变时调用此函数，来更新界面的绘制
	 */
	private void onTimeChanged() {
		mCalendar.setToNow();// 时间设置为当前时间
		// 下面是获取时、分、秒、日期和星期
		int hour = mCalendar.hour;
		int minute = mCalendar.minute;
		int second = mCalendar.second;
		mDay = String.valueOf(mCalendar.year) + "-"
				+ String.valueOf(mCalendar.month + 1) + "-"
				+ String.valueOf(mCalendar.monthDay);
		mWeek = this.getWeek(mCalendar.weekDay);

		mHour = hour + mMinutes / 60.0f + mSecond / 3600.0f;// 小时值，加上分和秒，效果会更加逼真
		mMinutes = minute + second / 60.0f;// 分钟值，加上秒，也是为了使效果逼真
		mSecond = second;

		mChanged = true;// 此时需要更新界面了

		updateContentDescription(mCalendar);// 作为一种辅助功能提供,为一些没有文字描述的View提供说明
	}

	@Override
	protected void onAttachedToWindow() {
		mTickerStopped = false;// 添加到窗口中就要更新时间了
		super.onAttachedToWindow();

		/**
		 * requests a tick on the next hard-second boundary
		 */
		mTicker = new Runnable() {
			public void run() {
				if (mTickerStopped)
					return;
				onTimeChanged();
				invalidate();
				long now = SystemClock.uptimeMillis();
				long next = now + (1000 - now % 1000);// 计算下次需要更新的时间间隔
				mHandler.postAtTime(mTicker, next);// 递归执行，就达到秒针一直在动的效果
			}
		};
		mTicker.run();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mTickerStopped = true;// 当view从当前窗口中移除时，停止更新
	}

	// @Override
	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// // 模式： UNSPECIFIED(未指定),父元素不对子元素施加任何束缚，子元素可以得到任意想要的大小；
	// // EXACTLY(完全)，父元素决定自元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小；
	// // AT_MOST(至多)，子元素至多达到指定大小的值。
	// // 根据提供的测量值(格式)提取模式(上述三个模式之一)
	// int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	// // 根据提供的测量值(格式)提取大小值(这个大小也就是我们通常所说的大小)
	// int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	// // 高度与宽度类似
	// int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	// int heightSize = MeasureSpec.getSize(heightMeasureSpec);
	//
	// float hScale = 1.0f;// 缩放值
	// float vScale = 1.0f;
	//
	// if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
	// hScale = (float) widthSize / (float) mDialWidth;//
	// 如果父元素提供的宽度比图片宽度小，就需要压缩一下子元素的宽度
	// }
	//
	// if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
	// vScale = (float) heightSize / (float) mDialHeight;// 同上
	// }
	//
	// float scale = Math.min(hScale, vScale);// 取最小的压缩值，值越小，压缩越厉害
	// // 最后保存一下，这个函数一定要调用
	// setMeasuredDimension(
	// resolveSizeAndState((int) (mDialWidth * scale),
	// widthMeasureSpec, 0),
	// resolveSizeAndState((int) (mDialHeight * scale),
	// heightMeasureSpec, 0));
	// }

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mChanged = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		boolean changed = mChanged;

		if (changed) {
			mChanged = false;
		}

		int px = getRight() - getLeft();// view可用宽度，通过右坐标减去左坐标
		int py = getBottom() - getTop();// view可用高度，通过下坐标减去上坐标

		int x = px / 2;// view宽度中心点坐标
		int y = py / 2;// view高度中心点坐标
		// int px = getMeasuredWidth();
		// int py = getMeasuredWidth();

		if (mDial != null && mHourHand != null && mMinuteHand != null && mSecondHand != null) {
			int w = mDial.getIntrinsicWidth();// 表盘宽度
			int h = mDial.getIntrinsicHeight();
			boolean scaled = false;
			// 最先画表盘，最底层的要先画上画板
			if (px < w || py < h) {// 如果view的可用宽高小于表盘图片，就要缩小图片
				scaled = true;
				float scale = Math.min((float) px / (float) w, (float) py
						/ (float) h);// 计算缩小值
				canvas.save();
				canvas.scale(scale, scale, x, y);// 实际上是缩小的画板
			}
			if (changed) {// 设置表盘图片位置。组件在容器X轴上的起点； 组件在容器Y轴上的起点； 组件的宽度；组件的高度
				mDial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
						+ (h / 2));
			}
			mDial.draw(canvas);// 这里才是真正把表盘图片画在画板上

			canvas.save();// 一定要保存一下
			// 再画时针
			canvas.rotate(mHour / 12.0f * 360.0f, x, y);// 旋转画板，第一个参数为旋转角度，第二、三个参数为旋转坐标点
			final Drawable hourHand = mHourHand;
			if (changed) {
				w = hourHand.getIntrinsicWidth();
				h = hourHand.getIntrinsicHeight();
				hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
						+ (h / 2));
			}
			hourHand.draw(canvas);// 把时针画在画板上
			canvas.restore();

			canvas.save();
			// 然后画分针
			canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);
			final Drawable minuteHand = mMinuteHand;
			if (changed) {
				w = minuteHand.getIntrinsicWidth();
				h = minuteHand.getIntrinsicHeight();
				minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
						+ (h / 2));
			}
			minuteHand.draw(canvas);
			canvas.restore();

			canvas.save();
			// 最后画秒针
			canvas.rotate(mSecond / 60.0f * 360.0f, x, y);
			final Drawable secondHand = mSecondHand;
			if (changed) {
				w = secondHand.getIntrinsicWidth();
				h = secondHand.getIntrinsicHeight();
				secondHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
						+ (h / 2));
			}
			secondHand.draw(canvas);
			canvas.restore();
			if (scaled) {
				canvas.restore();
			}

		} else {
			// 绘制表盘
			drawClockPandle(canvas, px, py);

			Paint paint = new Paint();
			/* 去锯齿 */
			paint.setAntiAlias(true);
			/* 设置paint的 style 为STROKE：空心 FILL 实心 */
			paint.setStyle(Paint.Style.FILL);
			/* 设置paint的外框宽度 */
			paint.setStrokeWidth(3);

			canvas.save();
			// 其次画日期
			mPaint.setTextSize(px / 8);
			if (changed) {
				int w = (int) (mPaint.measureText(mWeek));// 计算文字的宽度
				canvas.drawText(mWeek, (px / 2 - w / 2), py / 4, mPaint);// 画文字在画板上，位置为中间两个参数
				w = (int) (mPaint.measureText(mDay));
				canvas.drawText(mDay, (px / 2 - w / 2), py - (py / 4), mPaint);// 同上
			}
			canvas.rotate(mHour / 12.0f * 360.0f, x, y);// 旋转画板，第一个参数为旋转角度，第二、三个参数为旋转坐标点
			// 画一个实心三角形 时针
			paint.setColor(Color.GRAY);
			Path path1 = new Path();
			path1.moveTo(px / 2 - px / 25, py / 2);
			path1.lineTo(px / 2 + px / 25, py / 2);
			path1.lineTo(px / 2, py / 6);
			canvas.drawPath(path1, paint);
			canvas.restore();// 恢复画板到最初状态

			canvas.save();
			canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);
			// 画一个实心三角形 分针
			paint.setColor(Color.BLUE);
			Path path2 = new Path();
			path2.moveTo(px / 2 - px / 25, py / 2);
			path2.lineTo(px / 2 + px / 25, py / 2);
			path2.lineTo(px / 2, py / 12);
			canvas.drawPath(path2, paint);
			canvas.restore();

			canvas.save();
			canvas.rotate(mSecond / 60.0f * 360.0f, x, y);
			// 画一个实心三角形
			paint.setColor(Color.RED);
			Path path3 = new Path();
			path3.moveTo(px / 2 - px / 25, py / 2);
			path3.lineTo(px / 2 + px / 25, py / 2);
			path3.lineTo(px / 2, 0);
			canvas.drawPath(path3, paint);
			canvas.restore();
		}
	}

	/**
	 * 对这个view描述一下
	 * 
	 * @param time
	 */
	private void updateContentDescription(Time time) {
		final int flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;
		String contentDescription = DateUtils.formatDateTime(getContext(),
				time.toMillis(false), flags);
		setContentDescription(contentDescription);
	}

	/**
	 * 获取当前星期
	 * 
	 * @param week
	 * @return
	 */
	private String getWeek(int week) {
		switch (week) {
		case 1:
			return this.getContext().getString(R.string.monday);
		case 2:
			return this.getContext().getString(R.string.tuesday);
		case 3:
			return this.getContext().getString(R.string.wednesday);
		case 4:
			return this.getContext().getString(R.string.thursday);
		case 5:
			return this.getContext().getString(R.string.friday);
		case 6:
			return this.getContext().getString(R.string.saturday);
		case 0:
			return this.getContext().getString(R.string.sunday);
		default:
			return "";
		}
	}

	/**
	 * 纯电脑绘制表盘
	 * 
	 * @param canvas
	 */
	private void drawClockPandle(Canvas canvas, int px, int py) {
		Path path = null;
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		paint.setStrokeWidth(6);
		paint.setStyle(Style.STROKE);
		Paint paintPoint = null;
		int len = 0;
		int radius = 0;

		canvas.drawCircle(px / 2, py / 2, py / 40, paint);
		for (int i = 0; i < 90; i += 6) {
			canvas.save();
			if (i == 0 || i == 30 || i == 60) {
				paint.setStrokeWidth(6);
				path = new Path();
				len = py / 25;
				canvas.rotate(i, px / 2, py / 2);
				path.moveTo(-len / 2, py / 2);
				path.lineTo(len, py / 2);
				canvas.drawPath(path, paint);

				path.moveTo(px / 2, -len / 2);
				path.lineTo(px / 2, len);
				canvas.drawPath(path, paint);

				path.moveTo(px + len / 2, py / 2);
				path.lineTo(px - len, py / 2);
				canvas.drawPath(path, paint);

				path.moveTo(px / 2, py + len / 2);
				path.lineTo(px / 2, py - len);
				canvas.drawPath(path, paint);
				canvas.restore();
			} else {
				paintPoint = new Paint();
				paintPoint.setColor(Color.GREEN);
				paintPoint.setAntiAlias(true);
				canvas.rotate(i, px / 2, py / 2);
				radius = py / 60;
				canvas.drawCircle(1, py / 2, radius, paintPoint);
				canvas.drawCircle(px / 2, 1, radius, paintPoint);
				canvas.drawCircle(px - 1, py / 2, radius, paintPoint);
				canvas.drawCircle(px / 2, py - 1, radius, paintPoint);
				canvas.restore();
			}

		}

	}

	public void setmHourHand(Drawable mHourHand) {
		this.mHourHand = mHourHand;
	}

	public void setmMinuteHand(Drawable mMinuteHand) {
		this.mMinuteHand = mMinuteHand;
	}

	public void setmSecondHand(Drawable mSecondHand) {
		this.mSecondHand = mSecondHand;
	}

	public void setmDial(Drawable mDial) {
		this.mDial = mDial;
	}

}

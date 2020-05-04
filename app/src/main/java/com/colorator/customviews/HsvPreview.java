package com.colorator.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.colorator.R;

public class HsvPreview extends LinearLayout {
    LayoutInflater mInflater;
    View mColorGradientView;
    Paint mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public HsvPreview(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        init(context, null);
    }

    public HsvPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
        init(context, attrs);
    }

    public HsvPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View rootView = mInflater.inflate(R.layout.hsv_preview, this, true);
        setColorGradientView(context, attrs);
        mColorGradientView = rootView.findViewById(R.id.hsv_preview_rectangle);
    }

    private void setColorGradientView(Context context, AttributeSet attrs) {
        mColorGradientView = new View(context, attrs) {
            @Override
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                LinearGradient val = new LinearGradient(0, 0, 0, 50, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
                LinearGradient sat = new LinearGradient(0, 0, 50, 0, Color.WHITE, Color.RED, Shader.TileMode.CLAMP);
                ComposeShader merged = new ComposeShader(val, sat, PorterDuff.Mode.MULTIPLY);
                mGradientPaint.setShader(merged);
                canvas.drawRect(50, 50, 50, 50, mGradientPaint);
            }
        };
    }
}


//        LinearGradient val = new LinearGradient(0, 0, 0, 150, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
//        LinearGradient sat = new LinearGradient(0, 0, 150, 0, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
//        ComposeShader merged = new ComposeShader(val, sat, PorterDuff.Mode.MULTIPLY);
//        colorView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        colorView.setBackground(merged);

//        ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
//            @Override
//            public Shader resize(int width, int height) {
//                LinearGradient val = new LinearGradient(0, 0, 0, height, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
//                LinearGradient sat = new LinearGradient(0, 0, width, 0, Color.WHITE, Color.RED, Shader.TileMode.CLAMP);
//                ComposeShader merged = new ComposeShader(val, sat, PorterDuff.Mode.MULTIPLY);
//                return merged;
//            }
//        };
//        PaintDrawable p = new PaintDrawable();
//        p.setShape(new RectShape());
//        p.setShaderFactory(sf);
//        colorView.setBackground(p);

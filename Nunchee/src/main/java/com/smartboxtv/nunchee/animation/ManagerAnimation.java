package com.smartboxtv.nunchee.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;

/**
 * Created by Esteban- on 17-05-14.
 */
public  class ManagerAnimation {

    public static  void alpha(View v){

        ObjectAnimator animAlpha = ObjectAnimator.ofFloat(v, View.ALPHA, 0.8f);
        ObjectAnimator animAlpha2 = ObjectAnimator.ofFloat(v, View.ALPHA, 0.6f);
        ObjectAnimator animAlpha3 = ObjectAnimator.ofFloat(v, View.ALPHA, 0.4f);
        ObjectAnimator animAlpha4 = ObjectAnimator.ofFloat(v, View.ALPHA, 0.6f);
        ObjectAnimator animAlpha5 = ObjectAnimator.ofFloat(v, View.ALPHA, 0.8f);
        ObjectAnimator animAlpha6 = ObjectAnimator.ofFloat(v, View.ALPHA, 1f);

        AnimatorSet animEncuesta = new AnimatorSet();
        animEncuesta.playSequentially(animAlpha,animAlpha2,animAlpha3,animAlpha4,animAlpha5,animAlpha6);
        animEncuesta.setDuration(200);

        animEncuesta.start();
    }

    public static void anim(View v){

        float x = v.getX();
        ObjectAnimator moveToLef = ObjectAnimator.ofFloat(v, View.TRANSLATION_X, x);
        ObjectAnimator moveToRight = ObjectAnimator.ofFloat(v, View.TRANSLATION_X,x+20);
        AnimatorSet animView = new AnimatorSet();

        animView.playSequentially(moveToLef,moveToRight);
        //animView.setDuration();

        animView.start();
    }

    public static  void visibilityRight(final View v){


        ObjectAnimator animScaleXmin = ObjectAnimator.ofFloat(v, View.SCALE_X, 0f);



        AnimatorSet animView = new AnimatorSet();
        animView.play(animScaleXmin);
        animView.setDuration(200);
        animView.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ObjectAnimator animScaleXmax = ObjectAnimator.ofFloat(v, View.SCALE_X, 1f);
                AnimatorSet anim = new AnimatorSet();
                anim.play(animScaleXmax);
                anim.setDuration(200);
                anim.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        animView.start();
    }

    public static void moveToLeft(View v){


    }
    public static void moveToRight(View v){

        float x = v.getX();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(v, View.ALPHA, 0);
        ObjectAnimator moveToOrigen = ObjectAnimator.ofFloat(v, View.X, x - 129);
        ObjectAnimator moveToRight = ObjectAnimator.ofFloat(v, View.TRANSLATION_X,x);
        AnimatorSet animView = new AnimatorSet();

        //animView.playSequentially(alpha,moveToOrigen,moveToRight);
        animView.playSequentially(moveToOrigen,moveToRight);
        animView.setDuration(800);

        animView.start();
    }
    public static void transformerTranslation(View quieto, View mueve){

        quieto.setPivotX(0);
        quieto.setPivotY(0);

        float x1 = quieto.getX();
        float y1 = quieto.getY();

        Log.e("X ","--> "+x1);
        Log.e("Y ","--> "+y1);


        float anchoQuieto = quieto.getMeasuredWidth();
        float largoQuieto = quieto.getMeasuredHeight();

        float anchoMueve = mueve.getMeasuredWidth();
        float largoMueve = mueve.getMeasuredHeight();

        Log.e("anchoQuiero","--> "+anchoQuieto);
        Log.e("largoQuieto","--> "+largoQuieto);
        Log.e("anchoMueve","--> "+anchoMueve);
        Log.e("largoMueve","--> "+largoMueve);


        ObjectAnimator animTranslationX = ObjectAnimator.ofFloat(mueve, View.TRANSLATION_X, -(y1 +55+17)); // 657;
        ObjectAnimator animTranslationY = ObjectAnimator.ofFloat(mueve, View.TRANSLATION_Y, 164); // 164

        ObjectAnimator animTransformerWidth = ObjectAnimator.ofFloat(mueve, View.SCALE_X, porcentaje(anchoMueve,anchoQuieto));
        ObjectAnimator animTranslationHeight = ObjectAnimator.ofFloat(mueve, View.SCALE_Y, porcentaje(largoMueve, largoQuieto));

        Log.e("Porcentaje X","--> "+porcentaje(anchoMueve,anchoQuieto));
        Log.e("Porcentaje Y","--> "+porcentaje(largoMueve, largoQuieto));
        //ObjectAnimator animTransformerWidth = ObjectAnimator.ofFloat(mueve, View.SCALE_X, porcentaj() );
        //ObjectAnimator animTranslationHeight = ObjectAnimator.ofFloat(mueve, View.SCALE_Y, 0.169f);



        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1900);
        animatorSet.playTogether(animTranslationX, animTranslationY, animTransformerWidth, animTranslationHeight);
        //animatorSet.playTogether(animTranslationX, animTranslationY);
        //animatorSet.playTogether(animTransformerWidth, animTranslationHeight);
        animatorSet.start();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    public static void selection(final View v){
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(v, "alpha",  0.3f),
                ObjectAnimator.ofFloat(v, "scaleX", 1, 1.02f),
                ObjectAnimator.ofFloat(v, "scaleY", 1, 1.02f));
        set.setDuration(400);
        set.start();

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                AnimatorSet aux = new AnimatorSet();
                aux.playTogether(
                        ObjectAnimator.ofFloat(v, "alpha",  1f),
                        ObjectAnimator.ofFloat(v, "scaleX", 1.02f, 1f),
                        ObjectAnimator.ofFloat(v, "scaleY", 1.02f, 1f));
                aux.setDuration(400);
                aux.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    public static float porcentaje(float a, float b){

        return  (b / a);
    }
}

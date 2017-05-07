package com.misterright.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.misterright.util.AppUtil;

import java.util.ArrayList;

/**
 * Created by ruiaa on 2016/11/14.
 */

public class FragmentStack {

    private ArrayList<Fragment> stack;
    private FragmentManager fragmentManager;
    private Activity activity;
    private int fragmentContainId;

    public FragmentStack(Activity activity, int fragmentContainId) {
        this.activity = activity;
        this.fragmentContainId = fragmentContainId;
        this.fragmentManager=activity.getFragmentManager();
        this.stack =new ArrayList<>();
    }

    public ArrayList<Fragment> getStack() {
        return stack;
    }

    private void openFirstFragment(Fragment fragment){
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.add(fragmentContainId,fragment);
        transaction.commit();
        stack.add(fragment);
    }

    private void openNextFragment(Fragment fragment){
        AppUtil.hideKeyBoard(activity.getWindow().getDecorView());
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.add(fragmentContainId,fragment);
        transaction.hide(stack.get(stack.size()-1));
        transaction.commit();
        stack.add(fragment);
    }

    public void open(Fragment fragment){
        if (stack.isEmpty()){
            openFirstFragment(fragment);
        }else {
            openNextFragment(fragment);
        }
    }

    public boolean canTurnBack(){
        return stack.size()>=2;
    }

    public void turnBackFragment(){
        AppUtil.hideKeyBoard(activity.getWindow().getDecorView());
        int size= stack.size();
        if (size>=2){
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.remove(stack.get(size-1));
            transaction.show(stack.get(size-2));
            transaction.commit();
            stack.get(size-2).onResume();
            stack.remove(size-1);
        }else {
            activity.onBackPressed();
        }
    }

    public boolean replace(Fragment oldFragment,Fragment newFragment){
        if (!stack.contains(oldFragment)){
            return false;
        }else if (stack.get(stack.size()-1)==oldFragment){
            stack.add(newFragment);
            stack.remove(oldFragment);
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.remove(oldFragment);
            transaction.add(fragmentContainId,newFragment);
            transaction.commit();
        }else {
            int p=stack.indexOf(oldFragment);
            stack.remove(p);
            stack.add(p,newFragment);
        }
        return true;
    }

    public boolean addAndRemoveTop(Fragment fragment,int position){
        if (stack.size()<position){
            open(fragment);
            return false;
        }else {
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            for(int i=stack.size()-1;i>=position;i--){
                transaction.remove(stack.get(i));
                stack.remove(i);
            }
            transaction.add(fragmentContainId,fragment);
            stack.add(fragment);
            transaction.commit();
            return true;
        }
    }

    public boolean replaceAndRemoveTop(Fragment newFragment,Fragment oldFragment){
        if (!stack.contains(oldFragment)){
            return false;
        }else {
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            int p=stack.indexOf(oldFragment);
            return addAndRemoveTop(newFragment,p);
        }
    }

    public void clear(){
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        for (Fragment fragment:stack){
            transaction.remove(fragment);
        }
        stack.clear();
        transaction.commit();
    }

}

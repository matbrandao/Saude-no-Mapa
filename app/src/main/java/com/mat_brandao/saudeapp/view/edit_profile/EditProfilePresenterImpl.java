package com.mat_brandao.saudeapp.view.edit_profile;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.util.GenericObjectClickListener;
import com.mat_brandao.saudeapp.domain.util.MaskUtil;
import com.mat_brandao.saudeapp.view.register.AvatarAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class EditProfilePresenterImpl implements EditProfilePresenter, GenericObjectClickListener<Integer> {

    private final User mUser;
    private EditProfileInteractorImpl mInteractor;
    private Context mContext;
    private EditProfileView mView;

    private CompositeSubscription mSubscription = new CompositeSubscription();

    private String mName, mEmail, mSelectedSex, mCep, mPassword;
    private long mBirthDate;

    private BottomSheetDialog mBottomSheetDialog;
    private Integer mAvatarUrl = R.drawable.avatar_placeholder;
    private List<String> mSexList;
    private String mBio;

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mSubscription.unsubscribe();
        mView = null;
    }

    @Override
    public void onRetryClicked() {
        // TODO: 03-Oct-16
    }

    public EditProfilePresenterImpl(EditProfileView view, Context context) {
        mInteractor = new EditProfileInteractorImpl(context);
        mContext = context;
        mView = view;

        mUser = mInteractor.getUser();
        // TODO: 04/10/2016 show sex
        setupSpinners();
        setupObservables();

        showUserData();
    }

    private void showUserData() {
        if (!TextUtils.isEmpty(mUser.getGoogleToken())) {
            mPassword = mUser.getGoogleToken();
            mView.disablePasswordFields();
        } else if (!TextUtils.isEmpty(mUser.getFacebookToken())) {
            mPassword = mUser.getFacebookToken();
            mView.disablePasswordFields();
        } else {
            mPassword = "";
        }

        mName = mUser.getName();
        mEmail = mUser.getEmail();
        mSelectedSex = mUser.getSex();

        mView.setNameText(mUser.getName());
        mView.setEmailText(mUser.getEmail());
        mView.setCepText(MaskUtil.mask(MaskUtil.cepMask, mUser.getCep()));
        mView.setBioText(mUser.getBio());
        mView.setBirthDateText(mInteractor.parseDate(mUser.getBirthDate()));
        if (mUser.getSex().equals("M")) {
            mView.setSexSelecion(mSexList.indexOf(mContext.getString(R.string.male)));
        } else {
            mView.setSexSelecion(mSexList.indexOf(mContext.getString(R.string.female)));
        }
        mView.setPasswordText(mPassword);
        mView.setPasswordConfirmationText(mPassword);
        mView.loadImageToAvatar(mInteractor.getProfilePhotoUrl());
    }

    @Override
    public void onSaveFabClicked() {
        // TODO: 04/10/2016
    }

    @Override
    public void onAvatarClick() {
        setupBottomSheetDialog();
    }

    private void setupBottomSheetDialog() {
        mBottomSheetDialog = new BottomSheetDialog(mContext);
        View dialogView = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_bottom_sheet_profile, null);

        RecyclerView avatarRecycler = (RecyclerView) dialogView.findViewById(R.id.avatar_recycler);
        avatarRecycler.setHasFixedSize(true);
        avatarRecycler.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));

        avatarRecycler.setAdapter(new AvatarAdapter(mContext, this));

        mBottomSheetDialog.setContentView(dialogView);

        dialogView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
        mBehavior.setPeekHeight(dialogView.getMeasuredHeight() + 200);

        mBottomSheetDialog.show();
    }

    @Override
    public void onDateSet(int yy, int mm, int dd) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(yy, mm, dd);
        String date = String.format("%02d", dd) + "/" + String.format("%02d", mm + 1) + "/" + yy;
        mView.setBirthDateText(date);
    }

    @Override
    public void onBirthDateTouchListener() {
        mView.showDateDialog();
    }

    private void setupObservables() {
        Observable<Boolean> nameObservable = mView.registerNameObservable()
                .map(inputText -> {
                    mName = inputText.toString();
                    return inputText.toString().length() > 0 && inputText.toString().trim().length() > 3;
                })
                .skip(1)
                .distinctUntilChanged();

        Observable<Boolean> emailObservable = mView.registerEmailObservable()
                .map(inputText -> {
                    mEmail = inputText.toString();
                    return Patterns.EMAIL_ADDRESS.matcher(inputText).matches();
                })
                .skip(1)
                .distinctUntilChanged();

        Observable<Boolean> bioObservable = mView.registerNameObservable()
                .map(inputText -> {
                    if (inputText.toString().length() > 0 && inputText.toString().trim().length() > 3) {
                        mBio = inputText.toString();
                        return true;
                    } else {
                        return false;
                    }
                })
                .skip(1)
                .distinctUntilChanged();


        Observable<Boolean> cepObservable = mView.registerCepObservable()
                .map(inputText -> {
                    mCep = inputText.toString();
                    return inputText.toString().length() > 0 && inputText.toString().trim().length() > 8;
                })
                .skip(1)
                .distinctUntilChanged();

        Observable<Boolean> birthDateObservable = mView.registerBirthDateObservable()
                .map(inputText -> {
                    boolean result;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        mBirthDate = simpleDateFormat.parse(inputText.toString()).getTime();
                        result = true;
                    } catch (Exception e) {
                        result = false;
                    }
                    return result;
                })
                .skip(1)
                .distinctUntilChanged();

        Observable<Boolean> passwordObservable = mView.registerPasswordObservable()
                .map(inputText -> {
                    mPassword = inputText.toString();
                    return inputText.toString().length() < 6;
                })
                .skip(2)
                .distinctUntilChanged();

        Observable<Boolean> rePasswordObservable = mView.registerRePasswordObservable()
                .map(inputText -> !inputText.toString().equals(mPassword))
                .skip(1)
                .distinctUntilChanged();

        Observable<Integer> sexSpinnerObservable = mView.registerSexSpinnerObservable()
                .distinctUntilChanged();

        mSubscription.add(nameObservable.subscribe(isValid -> {
            mView.toggleNameError(isValid);
        }));

        mSubscription.add(emailObservable.subscribe(isValid -> {
            mView.toggleEmailError(isValid);
        }));

        mSubscription.add(cepObservable.subscribe(isValid -> {
            mView.toggleCepError(isValid);
        }));

        mSubscription.add(passwordObservable.subscribe(isValid -> {
            mView.togglePasswordError(!isValid);
        }));

        mSubscription.add(rePasswordObservable.subscribe(isValid -> {
            mView.toggleRePasswordError(!isValid);
        }));

        mSubscription.add(bioObservable.subscribe());

        mSubscription.add(sexSpinnerObservable.subscribe(integer -> {
            if (integer == 0) {
                mSelectedSex = "M";
            } else {
                mSelectedSex = "F";
            }
        }));

        mSubscription.add(Observable.combineLatest(
                nameObservable,
                birthDateObservable,
                cepObservable,
                emailObservable,
                passwordObservable,
                rePasswordObservable,
                (nameValid, ageIsValid, cepValid, emailValid, passValid, rePassValid) -> nameValid && ageIsValid
                        && cepValid && emailValid && !passValid && !rePassValid)
                .distinctUntilChanged()
                .subscribe(enabled -> {
                    mView.toggleFabButton(enabled);
                }));
    }

    private void setupSpinners() {
        mSexList = new ArrayList<>();
        mSexList.add(mContext.getString(R.string.male));
        mSexList.add(mContext.getString(R.string.female));
        mView.setSexSpinnerAdapter(new ArrayAdapter<>(mContext,
                android.R.layout.simple_spinner_dropdown_item,
                mSexList));
    }

    @Override
    public void onItemClick(Integer avatarDrawable) {
        mView.loadImageToAvatar(avatarDrawable);
        mAvatarUrl = avatarDrawable;
        mBottomSheetDialog.dismiss();
    }
}
package com.mat_brandao.saudeapp.view.edit_profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import com.mat_brandao.saudeapp.domain.util.DateUtil;
import com.mat_brandao.saudeapp.domain.util.GenericObjectClickListener;
import com.mat_brandao.saudeapp.domain.util.MaskUtil;
import com.mat_brandao.saudeapp.view.login.LoginActivity;
import com.mat_brandao.saudeapp.view.main.MainActivity;
import com.mat_brandao.saudeapp.view.register.AvatarAdapter;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class EditProfilePresenterImpl implements EditProfilePresenter, GenericObjectClickListener<Integer> {

    private final User mUser;
    private EditProfileInteractorImpl mInteractor;
    private Context mContext;
    private EditProfileView mView;

    private Observable mLastObservable;
    private Observer mLastObserver;
    private CompositeSubscription mSubscription = new CompositeSubscription();

    private String mName, mEmail, mSelectedSex, mCep;
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
        mView.showProgressDialog(mContext.getString(R.string.progress_updating_user));
        mSubscription.add(mLastObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mLastObserver));
    }

    public EditProfilePresenterImpl(EditProfileView view, Context context) {
        mInteractor = new EditProfileInteractorImpl(context);
        mContext = context;
        mView = view;

        mUser = mInteractor.getUser();
        setupSpinners();
        setupObservables();

        mView.disableFields();

        showUserData();
    }

    private void showUserData() {
        mName = mUser.getName();
        mEmail = mUser.getEmail();
        mSelectedSex = mUser.getSex();

        mView.setNameText(mUser.getName());
        mView.setEmailText(mUser.getEmail());
        mView.setCepText(MaskUtil.mask(MaskUtil.cepMask, MaskUtil.unmask(mUser.getCep())));
        mView.setBioText(mUser.getBio());
        mView.setBirthDateText(mInteractor.parseDate(mUser.getBirthDate()));
        if (mUser.getSex().equals("M")) {
            mView.setSexSelecion(mSexList.indexOf(mContext.getString(R.string.male)));
        } else {
            mView.setSexSelecion(mSexList.indexOf(mContext.getString(R.string.female)));
        }
        mView.loadImageToAvatar(mInteractor.getProfilePhotoUrl());
    }

    @Override
    public void onSaveFabClicked() {
        RxPermissions.getInstance(mContext)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(isGranted -> {
                    if (isGranted) {
                        if (mUser.getPasswordType() == User.FACEBOOK_LOGIN_TYPE) {
                            requestCreateFacebookUser();
                        } else if (mUser.getPasswordType() == User.GOOGLE_LOGIN_TYPE) {
                            requestCreateGoogleUser();
                        } else {
                            requestCreateNormalUser();
                        }
                    } else {
                        mView.showToast(mContext.getString(R.string.needed_permission_to_update_user));
                    }
                });
    }

    private void requestCreateNormalUser() {
        mLastObservable = mInteractor.requestUpdateNormalUser(mName, mEmail, mSelectedSex, mBio,
                MaskUtil.unmask(mCep), mBirthDate);
        mLastObserver = updateUserObserver;

        mView.showProgressDialog(mContext.getString(R.string.progress_creating_user));
        mSubscription.add(mInteractor.requestUpdateNormalUser(mName, mEmail, mSelectedSex, mBio, MaskUtil.unmask(mCep), mBirthDate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updateUserObserver));
    }

    private void requestCreateFacebookUser() {
        mLastObservable = mInteractor.requestUpdateFacebookUser(mName, mEmail, mSelectedSex, mBio,
                MaskUtil.unmask(mCep), mBirthDate);
        mLastObserver = updateUserObserver;

        mView.showProgressDialog(mContext.getString(R.string.progress_creating_user));
        mSubscription.add(mInteractor.requestUpdateFacebookUser(mName, mEmail, mSelectedSex, mBio, MaskUtil.unmask(mCep), mBirthDate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updateUserObserver));
    }

    private void requestCreateGoogleUser() {
        mLastObservable = mInteractor.requestUpdateGoogleUser(mName, mEmail, mSelectedSex, mBio,
                MaskUtil.unmask(mCep), mBirthDate);
        mLastObserver = updateUserObserver;

        mView.showProgressDialog(mContext.getString(R.string.progress_creating_user));
        mSubscription.add(mInteractor.requestUpdateGoogleUser(mName, mEmail, mSelectedSex, mBio, MaskUtil.unmask(mCep), mBirthDate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updateUserObserver));
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

    @Override
    public void onRemoveAccountClick() {
        mView.showRemoveAccountDialog(() -> {
            mView.showProgressDialog(mContext.getString(R.string.progress_wait));
            mSubscription.add(mInteractor.requestRemoveAccount()
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn(throwable -> null)
                    .subscribe(responseBodyResponse -> {
                        if (responseBodyResponse == null) {
                            mView.showToast(mContext.getString(R.string.http_error_no_connection));
                        } else {
                            if (responseBodyResponse.isSuccessful()) {
                                mView.showToast(mContext.getString(R.string.account_removed));
                                mInteractor.logout();
                                Intent intent = new Intent(mContext, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                mView.goToActivity(intent);
                            } else {
                                mView.showToast(mContext.getString(R.string.http_error_generic));
                            }
                        }
                    }));
        });
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

        Observable<Boolean> bioObservable = mView.registerBioObservable()
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
                    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("ddMMyyyy");
                    try {
                        mBirthDate = simpleDateFormat.parse(inputText.toString()).getTime();
                        result = true;
                    } catch (Exception e) {
                        result = false;
                        try {
                            mBirthDate = simpleDateFormat2.parse(inputText.toString()).getTime();
                            result = true;
                        } catch (Exception e1) {
                            result = false;
                        }
                    }

                    return result;
                })
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
                (nameValid, ageIsValid, cepValid, emailValid) -> nameValid && ageIsValid
                        && cepValid && emailValid)
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

    private Observer<Response<ResponseBody>> updateUserObserver = new Observer<Response<ResponseBody>>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            if (e instanceof UnknownHostException) {
                mView.showNoConnectionSnackBar();
            }
        }

        @Override
        public void onNext(Response<ResponseBody> response) {
            if (response.isSuccessful()) {
                try {
                    mView.showToast(response.body().string().replace("\"", ""));
                } catch (Exception e) {
                    mView.showToast(mContext.getString(R.string.http_success_update_user));
                }

                mInteractor.updateRealmUser(mName, mEmail, mSelectedSex, DateUtil.getDate(mBirthDate), mCep, mBio);

                if (mAvatarUrl != R.drawable.avatar_placeholder) {
                    mSubscription.add(mInteractor.requestSaveProfilePhoto(mAvatarUrl)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(saveProfilePhotoObserver));
                } else {
                    mView.dismissProgressDialog();
                }
            } else {
                mView.dismissProgressDialog();
                if (response.code() == 400) {
                    try {
                        mView.showToast(response.errorBody().string().replace("\"", ""));
                    } catch (IOException e) {
                        mView.showToast(mContext.getString(R.string.http_error_generic));
                    }
                } else {
                    mView.showToast(mContext.getString(R.string.http_error_500));
                }
            }
        }
    };

    private Observer<Response<ResponseBody>> saveProfilePhotoObserver = new Observer<Response<ResponseBody>>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            if (e instanceof UnknownHostException) {
                mView.showNoConnectionSnackBar();
            }
        }

        @Override
        public void onNext(Response<ResponseBody> responseBody) {
            mView.dismissProgressDialog();
            Picasso.with(mContext)
                    .invalidate(mInteractor.getProfilePhotoUrl());
        }
    };
}
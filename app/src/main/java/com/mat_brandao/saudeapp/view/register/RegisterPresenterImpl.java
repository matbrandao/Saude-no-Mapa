package com.mat_brandao.saudeapp.view.register;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Error401;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.util.GenericObjectClickListener;
import com.mat_brandao.saudeapp.domain.util.MaskUtil;
import com.mat_brandao.saudeapp.view.main.MainActivity;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.IOException;
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

public class RegisterPresenterImpl implements RegisterPresenter, GenericObjectClickListener<Integer> {
    private RegisterInteractorImpl mInteractor;
    private Context mContext;
    private RegisterView mView;

    private Observable mLastObservable;
    private Observer mLastObserver;
    private CompositeSubscription mSubscription = new CompositeSubscription();
    private String mName, mEmail, mSelectedSex, mCep, mPassword;
    private long mBirthDate;

    private User mUser;
    private BottomSheetDialog mBottomSheetDialog;
    private Integer mAvatarUrl = R.drawable.avatar_placeholder;

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mView = null;
        mSubscription.unsubscribe();
    }

    @Override
    public void onRetryClicked() {
        mView.showProgressDialog(mContext.getString(R.string.progress_creating_user));
        mSubscription.add(mLastObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mLastObserver));
    }

    public RegisterPresenterImpl(RegisterView view, Context context) {
        mInteractor = new RegisterInteractorImpl(context);
        mContext = context;
        mView = view;

        mUser = mInteractor.getUser();

        setupSpinners();
        setupObservables();

        if (mUser != null) {
            showFacebookData();
        }
    }

    private void showFacebookData() {
        mName = mUser.getName();
        mEmail = mUser.getEmail();
        mPassword = mUser.getPassword();
        mView.setNameText(mUser.getName());
        mView.setEmailText(mUser.getEmail());
        mView.setPasswordText(mUser.getPassword());
        mView.setPasswordConfirmationText(mUser.getPassword());
        mView.disableFields();
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
                .skip(1)
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
        List<String> mSexList = new ArrayList<>();
        mSexList.add(mContext.getString(R.string.male));
        mSexList.add(mContext.getString(R.string.female));
        mView.setSexSpinnerAdapter(new ArrayAdapter<>(mContext,
                android.R.layout.simple_spinner_dropdown_item,
                mSexList));
    }

    @Override
    public void onSaveFabClick() {
        RxPermissions.getInstance(mContext)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(isGranted -> {
                    if (isGranted) {
                        if (mUser == null) {
                            requestCreateNormalUser();
                        } else if (mUser.getPasswordType() == User.FACEBOOK_LOGIN_TYPE) {
                            requestCreateFacebookUser();
                        } else if (mUser.getPasswordType() == User.GOOGLE_LOGIN_TYPE) {
                            requestCreateGoogleUser();
                        }
                    } else {
                        mView.showToast(mContext.getString(R.string.needed_permission_to_create_user));
                    }
                });
    }

    private void requestCreateNormalUser() {
        mLastObservable = mInteractor.requestCreateNormalUser(mName, mEmail, mSelectedSex,
                mCep, mBirthDate, mPassword);
        mLastObserver = createUserObserver;

        mView.showProgressDialog(mContext.getString(R.string.progress_creating_user));
        mSubscription.add(mInteractor.requestCreateNormalUser(mName, mEmail, mSelectedSex, MaskUtil.unmask(mCep), mBirthDate, mPassword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUserObserver));
    }

    private void requestCreateFacebookUser() {
        mLastObservable = mInteractor.requestCreateFacebookUser(mName, mEmail, mSelectedSex,
                mCep, mBirthDate, mPassword);
        mLastObserver = createUserObserver;

        mView.showProgressDialog(mContext.getString(R.string.progress_creating_user));
        mSubscription.add(mInteractor.requestCreateFacebookUser(mName, mEmail, mSelectedSex, MaskUtil.unmask(mCep), mBirthDate, mPassword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUserObserver));
    }

    private void requestCreateGoogleUser() {
        mLastObservable = mInteractor.requestCreateGoogleUser(mName, mEmail, mSelectedSex,
                mCep, mBirthDate, mPassword);
        mLastObserver = createUserObserver;

        mView.showProgressDialog(mContext.getString(R.string.progress_creating_user));
        mSubscription.add(mInteractor.requestCreateGoogleUser(mName, mEmail, mSelectedSex, MaskUtil.unmask(mCep), mBirthDate, mPassword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUserObserver));
    }

    @Override
    public void onBirthDateTouchListener() {
        mView.showDateDialog();
    }

    @Override
    public void onDateSet(int yy, int mm, int dd) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(yy, mm, dd);
        String date = String.format("%02d", dd) + "/" + String.format("%02d", mm) + "/" + yy;
        mView.setBirthDateText(date);
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
    public void onItemClick(Integer avatarDrawable) {
        mView.loadImageToAvatar(avatarDrawable);
        mAvatarUrl = avatarDrawable;
        mBottomSheetDialog.dismiss();
    }

    Observer<Response<ResponseBody>> createUserObserver = new Observer<Response<ResponseBody>>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            mView.showNoConnectionSnackBar();
        }

        @Override
        public void onNext(Response<ResponseBody> response) {
            mView.dismissProgressDialog();

            if (response.isSuccessful()) {
                try {
                    mView.showToast(response.body().string().replace("\"", ""));
                } catch (Exception e) {
                    mView.showToast(mContext.getString(R.string.http_success_register_user));
                }
                if (mUser == null) {
                    mView.showProgressDialog(mContext.getString(R.string.progress_logging_in));
                    mSubscription.add(mInteractor
                            .requestLoginWithAccount(mEmail, mPassword)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(loginObserver));
                } else if (mUser.getPasswordType() == User.FACEBOOK_LOGIN_TYPE) {
                    mView.showProgressDialog(mContext.getString(R.string.progress_logging_in));
                    mSubscription.add(mInteractor
                            .requestLoginWithFacebook(mEmail, mPassword)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(loginObserver));
                } else if (mUser.getPasswordType() == User.GOOGLE_LOGIN_TYPE) {
                    mView.showProgressDialog(mContext.getString(R.string.progress_logging_in));
                    mSubscription.add(mInteractor
                            .requestLoginWithGoogle(mEmail, mPassword)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(loginObserver));
                }
            } else {
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

    Observer<Response<User>> loginObserver = new Observer<Response<User>>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            mView.dismissProgressDialog();
            mView.showNoConnectionSnackBar();
        }

        @Override
        public void onNext(Response<User> userResponse) {
            if (!userResponse.isSuccessful()) {
                if (userResponse.code() == 401) {
                    try {
                        Error401 error401 = new Gson().fromJson(userResponse.errorBody().string(), Error401.class);
                        mView.showToast(error401.getMessageList().get(0).getText() + ".\nVerifique seus dados");
                    } catch (Exception e) {
                        mView.showToast(mContext.getString(R.string.http_error_generic));
                    }
                } else {
                    mView.showToast(mContext.getString(R.string.http_error_500));
                }
            } else {
                User user = userResponse.body();
                user.setAppToken(userResponse.headers().get("appToken"));
                if (mUser != null)
                    user.setPasswordType(mUser.getPasswordType());
                mInteractor.saveUserToRealm(user);
                mUser = mInteractor.getUser();

                mSubscription.add(mInteractor.requestSaveProfilePhoto(mAvatarUrl)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(saveProfilePhotoObserver));
            }
        }
    };

    Observer<Response<ResponseBody>> saveProfilePhotoObserver = new Observer<Response<ResponseBody>>() {
        @Override
        public void onCompleted() {
            mView.dismissProgressDialog();
        }

        @Override
        public void onError(Throwable e) {
            mView.dismissProgressDialog();
            Timber.i("onError() called with: e = [" + e + "]");
        }

        @Override
        public void onNext(Response<ResponseBody> responseBody) {
            Timber.i("onNext() called with: responseBody = [" + responseBody + "]");
            if (responseBody.isSuccessful()) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                mView.goToActivity(intent);
            }
        }
    };
}
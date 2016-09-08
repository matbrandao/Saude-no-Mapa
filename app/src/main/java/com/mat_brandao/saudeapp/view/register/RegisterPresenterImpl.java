package com.mat_brandao.saudeapp.view.register;

import android.content.Context;
import android.util.Patterns;
import android.widget.ArrayAdapter;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.util.MaskUtil;

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

public class RegisterPresenterImpl implements RegisterPresenter {
    private RegisterInteractorImpl mInteractor;
    private Context mContext;
    private RegisterView mView;

    private Observable mLastObservable;
    private Observer mLastObserver;
    private CompositeSubscription mSubscription = new CompositeSubscription();
    private String mName, mEmail, mSelectedSex, mCep, mPassword;
    private long mBirthDate;

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

        setupSpinners();
        setupObservables();
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
        mLastObservable = mInteractor.requestCreateUser(mName, mEmail, mSelectedSex,
                mCep, mBirthDate, mPassword);
        mLastObserver = createUserObserver;

        mView.showProgressDialog(mContext.getString(R.string.progress_creating_user));
        mSubscription.add(mInteractor.requestCreateUser(mName, mEmail, mSelectedSex, MaskUtil.unmask(mCep), mBirthDate, mPassword)
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

    Observer<Response<ResponseBody>> createUserObserver = new Observer<Response<ResponseBody>>() {
        @Override
        public void onCompleted() {
            mView.dismissProgressDialog();
            Timber.i("onCompleted() called");
        }

        @Override
        public void onError(Throwable e) {
            mView.dismissProgressDialog();
            mView.showNoConnectionSnackBar();
        }

        @Override
        public void onNext(Response<ResponseBody> response) {
            if (response.isSuccessful()) {
                try {
                    mView.showToast(response.body().string().replace("\"", ""));
                } catch (Exception e) {
                    mView.showToast(mContext.getString(R.string.http_success_register_user));
                }
                mView.finishActivity();
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
}
package com.tng.assistance.tangdou.infrastructure;


import java.util.Objects;

import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;

@Singleton
public final class AndroidBus {
    private final BehaviorSubject<Object> subject = BehaviorSubject.create();
    private static AndroidBus INSTANCE;
    private AndroidBus() {
    }

    public static AndroidBus getInstance() {
        if (INSTANCE == null) {
            synchronized (AndroidBus.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AndroidBus();
                }
            }
        }
        return INSTANCE;
    }

    public Disposable subscribe(Consumer<Object> consumer, Predicate<Object> filter) {
        Objects.requireNonNull(consumer);
        return subject
                .filter(filter)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    public void publish(Object event) {
        subject.onNext(event);
    }


}

package com.alibaba.android.arouter.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.demo.module1.testactivity.TestDynamicActivity;
import com.alibaba.android.arouter.demo.module1.testservice.SingleService;
import com.alibaba.android.arouter.demo.service.HelloService;
import com.alibaba.android.arouter.demo.service.model.TestObj;
import com.alibaba.android.arouter.demo.service.model.TestParcelable;
import com.alibaba.android.arouter.demo.service.model.TestSerializable;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.facade.enums.RouteType;
import com.alibaba.android.arouter.facade.model.RouteMeta;
import com.alibaba.android.arouter.facade.template.IRouteGroup;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        // Build test data.
        TestSerializable testSerializable = new TestSerializable("Titanic", 555);
        TestParcelable testParcelable = new TestParcelable("jack", 666);
        TestObj testObj = new TestObj("Rose", 777);
        List<TestObj> objList = new ArrayList<>();
        objList.add(testObj);
        Map<String, List<TestObj>> map = new HashMap<>();
        map.put("testMap", objList);

        switch (v.getId()) {
            case R.id.openLog:
                ARouter.openLog();
                break;
            case R.id.openDebug:
                ARouter.openDebug();
                break;
            case R.id.init:
                // 调试模式不是必须开启，但是为了防止有用户开启了InstantRun，但是
                // 忘了开调试模式，导致无法使用Demo，如果使用了InstantRun，必须在
                // 初始化之前开启调试模式，但是上线前需要关闭，InstantRun仅用于开
                // 发阶段，线上开启调试模式有安全风险，可以使用BuildConfig.DEBUG
                // 来区分环境
                ARouter.openDebug();
                ARouter.init(getApplication());
                break;
            case R.id.normalNavigation:
                ARouter.getInstance()
                        .build("/test/activity2")
                        .navigation();

                // 也可以通过依赖对方提供的二方包来约束入参
                // 非必须，可以通过这种方式调用
                // Entrance.redirect2Test1Activity("张飞", 48, this);
                break;
            case R.id.kotlinNavigation:
                ARouter.getInstance()
                        .build("/kotlin/test")
                        .withString("name", "老王")
                        .withInt("age", 23)
                        .navigation();
                break;
            case R.id.normalNavigationWithParams:
                // ARouter.getInstance()
                //         .build("/test/activity2")
                //         .withString("key1", "value1")
                //         .navigation();

                Uri testUriMix = Uri.parse("arouter://m.aliyun.com/test/activity2");
                ARouter.getInstance().build(testUriMix)
                        .withString("key1", "value1")
                        .navigation();

                break;
            case R.id.oldVersionAnim:
                ARouter.getInstance()
                        .build("/test/activity2")
                        .withTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        .navigation(this);
                break;
            case R.id.newVersionAnim:
                if (Build.VERSION.SDK_INT >= 16) {
                    ActivityOptionsCompat compat = ActivityOptionsCompat.
                            makeScaleUpAnimation(v, v.getWidth() / 2, v.getHeight() / 2, 0, 0);

                    ARouter.getInstance()
                            .build("/test/activity2")
                            .withOptionsCompat(compat)
                            .navigation();
                } else {
                    Toast.makeText(this, "API < 16,不支持新版本动画", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.interceptor:
                ARouter.getInstance()
                        .build("/test/activity4")
                        .navigation(this, new NavCallback() {
                            @Override
                            public void onArrival(Postcard postcard) {

                            }

                            @Override
                            public void onInterrupt(Postcard postcard) {
                                Log.d("ARouter", "被拦截了");
                            }
                        });
                break;
            case R.id.navByUrl:
                ARouter.getInstance()
                        .build("/test/webview")
                        .withString("url", "file:///android_asset/scheme-test.html")
                        .navigation();
                break;
            case R.id.autoInject:
                ARouter.getInstance().build("/test/activity1")
                        .withString("name", "老王")
                        .withInt("age", 18)
                        .withBoolean("boy", true)
                        .withLong("high", 180)
                        .withString("url", "https://a.b.c")
                        .withSerializable("ser", testSerializable)
                        .withParcelable("pac", testParcelable)
                        .withObject("obj", testObj)
                        .withObject("objList", objList)
                        .withObject("map", map)
                        .navigation();
                break;
            case R.id.navByName:
                ((HelloService) ARouter.getInstance().build("/yourservicegroupname/hello").navigation()).sayHello("mike");
                break;
            case R.id.navByType:
                ARouter.getInstance().navigation(HelloService.class).sayHello("mike");
                break;
            case R.id.navToMoudle1:
                ARouter.getInstance().build("/module/1").navigation();
                break;
            case R.id.navToMoudle2:
                // 这个页面主动指定了Group名
                ARouter.getInstance().build("/module/2", "m2").navigation();
                break;
            case R.id.destroy:
                ARouter.getInstance().destroy();
                break;
            case R.id.failNav:
                ARouter.getInstance().build("/xxx/xxx").navigation(this, new NavCallback() {
                    @Override
                    public void onFound(Postcard postcard) {
                        Log.d("ARouter", "找到了");
                    }

                    @Override
                    public void onLost(Postcard postcard) {
                        Log.d("ARouter", "找不到了");
                    }

                    @Override
                    public void onArrival(Postcard postcard) {
                        Log.d("ARouter", "跳转完了");
                    }

                    @Override
                    public void onInterrupt(Postcard postcard) {
                        Log.d("ARouter", "被拦截了");
                    }
                });
                break;
            case R.id.callSingle:
                ARouter.getInstance().navigation(SingleService.class).sayHello("Mike");
                break;
            case R.id.failNav2:
                ARouter.getInstance().build("/xxx/xxx").navigation();
                break;
            case R.id.failNav3:
                ARouter.getInstance().navigation(MainActivity.class);
                break;
            case R.id.normalNavigation2:
                ARouter.getInstance()
                        .build("/test/activity2")
                        .navigation(this, 666);
                break;
            case R.id.getFragment:
                Fragment fragment = (Fragment) ARouter.getInstance().build("/test/fragment")
                        .withString("name", "老王")
                        .withInt("age", 18)
                        .withBoolean("boy", true)
                        .withLong("high", 180)
                        .withString("url", "https://a.b.c")
                        .withSerializable("ser", testSerializable)
                        .withParcelable("pac", testParcelable)
                        .withObject("obj", testObj)
                        .withObject("objList", objList)
                        .withObject("map", map).navigation();
                Toast.makeText(this, "找到Fragment:" + fragment.toString(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.addGroup:
                ARouter.getInstance().addRouteGroup(new IRouteGroup() {
                    @Override
                    public void loadInto(Map<String, RouteMeta> atlas) {
                        atlas.put("/dynamic/activity", RouteMeta.build(
                                RouteType.ACTIVITY,
                                TestDynamicActivity.class,
                                "/dynamic/activity",
                                "dynamic", 0, 0));
                    }
                });
                break;
            case R.id.dynamicNavigation:
                // 该页面未配置 Route 注解，动态注册到 ARouter
                ARouter.getInstance().build("/dynamic/activity")
                        .withString("name", "老王")
                        .withInt("age", 18)
                        .withBoolean("boy", true)
                        .withLong("high", 180)
                        .withString("url", "https://a.b.c")
                        .withSerializable("ser", testSerializable)
                        .withParcelable("pac", testParcelable)
                        .withObject("obj", testObj)
                        .withObject("objList", objList)
                        .withObject("map", map).navigation(this);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 666:
                Log.e("activityResult", String.valueOf(resultCode));
                break;
            default:
                break;
        }
    }
}

package cse.SUSTC.ParkingApp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.Objects;


/**
 * This part will load user stage that can achieve user functions in our app
 * not finished yet
 */
public class UsrStage extends Fragment {
    private static final int REQUEST_CODE = 1;

    QMUIGroupListView mGroupListContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_usr_stage, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGroupListContact = Objects.requireNonNull(getActivity()).findViewById(R.id.group_list_item_contact);

        if (mGroupListContact.getSectionCount() == 0) {

            QMUICommonListItemView carManagement = mGroupListContact.createItemView("我的信息");
            QMUICommonListItemView feedbackOp = mGroupListContact.createItemView("反馈");
            QMUICommonListItemView qrCodeOp = mGroupListContact.createItemView("设置");

            View.OnClickListener openMail = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof QMUICommonListItemView) {
                        Uri uri = Uri.parse("mailto:support@parkingApp.com");
                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback or Appeal"); // 主题
                        intent.putExtra(Intent.EXTRA_TEXT, ""); // 正文
                        startActivity(Intent.createChooser(intent, "请选择邮件类应用"));

                    }
                }
            };

            View.OnClickListener openQRCodeScan = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof QMUICommonListItemView) {
                        Intent intent = new Intent(getActivity(), CaptureActivity.class);
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                }
            };

            View.OnClickListener jumpCarManagement = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof QMUICommonListItemView) {
                        Intent intent = new Intent(getActivity(), CarManagementActivity.class);
                        startActivity(intent);

                    }
                }
            };

            int size = QMUIDisplayHelper.dp2px(Objects.requireNonNull(getContext()), 20);
            QMUIGroupListView.newSection(getContext())
                    .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                    .addItemView(feedbackOp, openMail)
                    .addItemView(qrCodeOp, openQRCodeScan)
                    .addItemView(carManagement, jumpCarManagement)
                    .addTo(mGroupListContact);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}

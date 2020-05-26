package Page2_1_X;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.hansol.spot_200510_hs.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page2_1_X_ImageFragment extends Fragment {

    Context context;
    String imgList[] = new String[100];

    String imgList2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page2_1_x_fragment_image, container, false);

        ImageView imageView = view.findViewById(R.id.page2_1_1_imageView);

        if (getArguments() != null) {
            Bundle args = getArguments();
//            // Page2_1_1에서 받아온 리소스 이미지뷰에 셋팅
//            imageView.setImageResource(args.getInt("imgRes"));
            imgList2 = args.getString("imgRes");
            // intent로 넘길 거 리스트 받아오기
            imgList = args.getStringArray("imgRes3");

            if (getContext() != null) {
                if (imgList2 != null) {
//                    for (int i = 0; i < imgList.length; i++) {
//                        Log.i("받으라고 ㅜ ㅜ", imgList[i]);
//                    }

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
                    Glide.with(getContext()).load(imgList2).apply(requestOptions).into(imageView);

//                    for (int i = 0; i < imgList.length; i++) {
//                        //이미지뷰에 url 이미지 넣기.
//                        Glide.with(getContext()).load(imgList).centerCrop().into(imageView);
//                    }
                }
            }

            imageView.setImageResource(args.getInt("imgRes2"));
        }

        // 이미지 뷰에 클릭 이벤트 넣기
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 레이아웃들 위에 겹쳐서 올리기
                Toast.makeText(getContext(), "눌렸음", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), Page2_1_X_ImageSelected.class);
                intent.putExtra("setImgRes", imgList);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        return view;
    }
}

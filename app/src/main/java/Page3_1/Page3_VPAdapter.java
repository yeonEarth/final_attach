package Page3_1;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class Page3_VPAdapter extends FragmentPagerAdapter {
    String text;

    Page3_VPAdapter(FragmentManager fm){
        super(fm);
    } //꼭 있어야함

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return Page3_1_fragment1.newInstance(text);
            case 1:
                return Page3_1_fragment2.newInstance(text);
        }
        return null;
    }
    private static int PAGE_NUMBER=2; //생성할 프래그먼트 수
    @Override
    public int getCount() {
        return PAGE_NUMBER;
    }

    public void getText(String text){
        this.text = text;
    }

    //탭 레이아웃의 제목을 지정해준다
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "경로 상세보기";
            case 1:
                return"지도로 보기";
            default:
                return null;
        }
    }
}


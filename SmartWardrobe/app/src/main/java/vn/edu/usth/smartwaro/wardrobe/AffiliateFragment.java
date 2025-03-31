package vn.edu.usth.smartwaro.wardrobe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import vn.edu.usth.smartwaro.R;

public class AffiliateFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_affiliate, container, false);

        Button viewOnlineButton = view.findViewById(R.id.view_online_button);
        ImageView productImageView = view.findViewById(R.id.product_image);

        Bundle args = getArguments();
        String affiliateUrl = args != null ? args.getString("affiliate_url") : "";
        int productImageResId = args != null ? args.getInt("product_image", -1) : -1;

        if (productImageResId != -1) {
            productImageView.setImageResource(productImageResId);
        }

        viewOnlineButton.setOnClickListener(v -> {
            if (!affiliateUrl.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(affiliateUrl));
                startActivity(browserIntent);
            }
        });

        return view;
    }
}

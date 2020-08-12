package leadinguniversity.pocketlibraryadmin.Ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import leadinguniversity.pocketlibraryadmin.R;
import leadinguniversity.pocketlibraryadmin.Util.ActivityLancher;
import leadinguniversity.pocketlibraryadmin.data.Book;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BookDetailsFragment extends Fragment {
    StorageReference storageRef ;
    private Book book;
    @BindView(R.id.bookIV)
    ImageView bookIV;
    @BindView(R.id.titleTV)
    TextView titleTV;
    @BindView(R.id.descriptionTV)
    TextView descriptionTV;
    @BindView(R.id.priceTV)
    TextView priceTV;
    @BindView(R.id.dateTV)
    TextView dateTV;
    @BindView(R.id.read_bookB)
    Button readbookB;


    public BookDetailsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static BookDetailsFragment with(Book book) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ActivityLancher.BOOK_KEY, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = (Book) getArguments().getSerializable(ActivityLancher.BOOK_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_details, container, false);
        ButterKnife.bind(this, view);

        android.support.v7.widget.Toolbar toolbar = view.findViewById(R.id.toolbar2);
        toolbar.setTitle(book.getTitle());

        descriptionTV.setText(book.getDescription());
        titleTV.setText(book.getTitle());
        priceTV.setText(String.valueOf(book.getPrice()));
        dateTV.setText(book.getDate());
        Glide.with(getContext()).load(book.getImageUrl()).into(bookIV);
        return view;
    }
      @OnClick(R.id.read_bookB)
    public void readbook() {
          ActivityLancher.openPDFViewerActivity(getContext(), book);
    }


}

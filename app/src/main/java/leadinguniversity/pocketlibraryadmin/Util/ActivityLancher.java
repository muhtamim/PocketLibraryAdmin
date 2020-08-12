package leadinguniversity.pocketlibraryadmin.Util;

import android.content.Context;
import android.content.Intent;

import leadinguniversity.pocketlibraryadmin.Ui.AddBookActivity;
import leadinguniversity.pocketlibraryadmin.Ui.BooksActivity;
import leadinguniversity.pocketlibraryadmin.Ui.EditBookActivity;
import leadinguniversity.pocketlibraryadmin.Ui.EditProfileActivity;
import leadinguniversity.pocketlibraryadmin.Ui.EditPublisherActivity;
import leadinguniversity.pocketlibraryadmin.Ui.LoginActivity;
import leadinguniversity.pocketlibraryadmin.Ui.PDFViewerActivity;
import leadinguniversity.pocketlibraryadmin.Ui.RegisterActivity;
import leadinguniversity.pocketlibraryadmin.data.Book;
import leadinguniversity.pocketlibraryadmin.data.Publisher;



public final class ActivityLancher {
    public static final String BOOK_KEY = "book";
    public static final String publisher_KEY = "publisher";


    public static void openLoginActivity(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));

    }

    public static void openBooksActivity(Context context) {
        context.startActivity(new Intent(context, BooksActivity.class));

    }
    public static void openRegisterActivity(Context context) {
        context.startActivity(new Intent(context, RegisterActivity.class));

    }
    public static void openAddBookActivity(Context context){
        Intent i = new Intent(context, AddBookActivity.class);
        context.startActivity(i);
    }
    public static void openEditBookActivity(Context context, Book book){
        Intent i = new Intent(context, EditBookActivity.class);
        i.putExtra(BOOK_KEY, book);
        context.startActivity(i);
    }

    public static void openEditpublisherFragment(Context context, Publisher publisher){
        Intent i = new Intent(context, EditPublisherActivity.class);
        i.putExtra("publisher_KEY", publisher);
        context.startActivity(i);
    }

    public static void openEditPublisherActivity(Context context, Publisher publisher){
        Intent i = new Intent(context, EditProfileActivity.class);
        i.putExtra("publisher_KEY", publisher);
        context.startActivity(i);
    }
    public static void openPDFViewerActivity(Context context, Book book){
        Intent i = new Intent(context, PDFViewerActivity.class);
        i.putExtra(BOOK_KEY, book);
        context.startActivity(i);
    }


}

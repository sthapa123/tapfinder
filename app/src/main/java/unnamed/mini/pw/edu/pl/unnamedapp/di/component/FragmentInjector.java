package unnamed.mini.pw.edu.pl.unnamedapp.di.component;

import unnamed.mini.pw.edu.pl.unnamedapp.view.findbeer.FindBeerFragment;
import unnamed.mini.pw.edu.pl.unnamedapp.view.findbeer.FindBeerResultsFragment;
import unnamed.mini.pw.edu.pl.unnamedapp.view.map.MapFragment;
import unnamed.mini.pw.edu.pl.unnamedapp.view.place.beers.PlaceBeersFragment;
import unnamed.mini.pw.edu.pl.unnamedapp.view.place.general.PlaceGeneralFragment;
import unnamed.mini.pw.edu.pl.unnamedapp.view.place.specialoffers.PlaceSpecialOffersFragment;
import unnamed.mini.pw.edu.pl.unnamedapp.view.profile.MyProfileFragment;

public interface FragmentInjector {
    void inject(MapFragment mapFragment);
    void inject(PlaceGeneralFragment placeDetailsGeneralFragment);
    void inject(PlaceBeersFragment placeBeersFragment);
    void inject(PlaceSpecialOffersFragment placeSpecialOffersFragment);
    void inject(FindBeerFragment findBeerFragment);
    void inject(FindBeerResultsFragment findBeerResultsFragment);
    void inject(MyProfileFragment myProfileFragment);
}

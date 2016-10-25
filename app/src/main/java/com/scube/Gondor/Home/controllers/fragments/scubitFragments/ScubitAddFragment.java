package com.scube.Gondor.Home.controllers.fragments.scubitFragments;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.scube.Gondor.Core.controllers.AppController;
import com.scube.Gondor.Core.controllers.NavigationFragment;
import com.scube.Gondor.Helpers.api.ScubitApiHelper;
import com.scube.Gondor.Home.models.Brand;
import com.scube.Gondor.Home.models.Mall;
import com.scube.Gondor.Home.models.ShopProfile;
import com.scube.Gondor.R;
import com.scube.Gondor.Home.models.scubitModels.Offer;
import com.scube.Gondor.Home.models.scubitModels.PaymentType;
import com.scube.Gondor.Home.models.scubitModels.PriceRange;
import com.scube.Gondor.Home.views.scubitViews.OfferAdapter;
import com.scube.Gondor.Home.views.scubitViews.PaymentTypeAdapter;
import com.scube.Gondor.Home.views.scubitViews.PriceRangeAdapter;
import com.scube.Gondor.Home.views.scubitViews.ScubitSpinnerAdapter;
import com.scube.Gondor.Util.DialogUtils;
import com.scube.Gondor.Util.GlobalUtils;
import com.scube.Gondor.Util.Interfaces.ApiResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by vashoka on 6/26/15.
 */
public class ScubitAddFragment extends NavigationFragment {

    Context context;
    Spinner offersSpinner, priceRangesSpinner, paymentTypesSpinner, mallNamesSpinner, shopNamesSpinner, brandNamesSpinner;
    ArrayList<Offer> offers = new ArrayList<Offer>();
    ArrayList<PriceRange> priceRanges = new ArrayList<PriceRange>();
    ArrayList<PaymentType> paymentTypes = new ArrayList<PaymentType>();
    OfferAdapter offersAdapter;
    PriceRangeAdapter priceRangeAdapter;
    PaymentTypeAdapter paymentTypeAdapter;

    ArrayList<Mall> malls = new ArrayList<Mall>();
    ArrayList<ShopProfile> shopProfiles = new ArrayList<ShopProfile>();
    ArrayList<Brand> brands = new ArrayList<Brand>();
    ScubitSpinnerAdapter mallsAdapter, brandsAdapter, shopProfileAdapter;
//    ShopProfileAdapter shopProfileAdapter;
//    BrandAdapter brandsAdapter;

    Button addScubitButton;
    EditText startTime, endTime, startDate, endDate;
    View thisView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        thisView = inflater.inflate(R.layout.fragment_scubit_add, container, false);
        context = getActivity().getApplicationContext();

        // Offers
        offersSpinner = (Spinner) thisView.findViewById(R.id.offers);
        offersAdapter = new OfferAdapter(this, context, offers, context.getResources().getInteger(R.integer.offer_spinner_item_mode));
        offersSpinner.setAdapter(offersAdapter);

        // Price Ranges
        priceRangesSpinner = (Spinner) thisView.findViewById(R.id.priceRanges);
        priceRangeAdapter = new PriceRangeAdapter(this, context, priceRanges, context.getResources().getInteger(R.integer.price_range_spinner_item_mode));
        priceRangesSpinner.setAdapter(priceRangeAdapter);

        // Payment Types
        paymentTypesSpinner = (Spinner) thisView.findViewById(R.id.paymentTypes);
        paymentTypeAdapter = new PaymentTypeAdapter(this, context, paymentTypes, context.getResources().getInteger(R.integer.payment_type_spinner_item_mode));
        paymentTypesSpinner.setAdapter(paymentTypeAdapter);

        // Fetch data for each of the data lists
        if(true) {
            getOffers();
            getPriceRanges();
            getPaymentTypes();
        } else {
            // Fetch all values in single API call
            getAllScubitContextValues();
        }

        // Date and Time picker
        setupTimeAndDatePickers();

        // Load Mall Names
        loadMallNames();

        // Add sbubit button
        addScubitButton = (Button) thisView.findViewById(R.id.addScubitButton);
        addScubitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject addScubitObject = validateAndGetScubitObject();

                ScubitApiHelper scubitApiHelper = new ScubitApiHelper(context, "ScubitAddFragment");
                scubitApiHelper.addScubit(addScubitObject, new ApiResponse.Listener<Boolean>() {

                    @Override
                    public void onResponse(Boolean addScubitStatus) {
                        if(addScubitStatus) {
                            // Scubit successfully Added
                            Log.d(getString(R.string.Scubit_Add_Fragment), "Scubit added successfully");

                            // navigate to my scubits Fragment
                            navigationController.pushFragmentToStack(MyScubitsFragment.class, null, null);


                        } else {
                            DialogUtils.showLong(context, "Adding Scubit Failed!");
                        }
                    }
                });
            }
        });

        // Add tag for accessibility
        thisView.setTag("ScubitAddFragment");

        return thisView;
    }

    private void getOffers() {

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + getString(R.string.get_scubits_context);

        // This api end point gives all the type of scubits offers that we have.
        apiEndPoint += "?q_case=1";

        Log.d(getString(R.string.Scubit_Add_Fragment), apiEndPoint);

        // Create Volley request to fetch the scubit offers
        JsonObjectRequest getBrandsBasedOnLocReq = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray offersArray = new JSONArray();
                        try {
                            offersArray = response.getJSONArray("offers");
                            Log.d(getString(R.string.Scubit_Add_Fragment), offersArray.toString());
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        /* Get offers endpoint response
                                   JSON-API format
                        {
                           offers: [
                            {
                                 offer_id: 1,
                                 offer_name: "Buy 1 get 1 free"
                            },
                            {
                                 offer_id: 2,
                                 offer_name: "Buy 1 get 2nd 50% off"
                            },
                            {
                                 offer_id: 4,
                                 offer_name: "Buy 2 get 1 free"
                            },
                            {
                                offer_id: 3,
                                offer_name: "Buy for 1000 INR and get 2nd free"
                            }
                         ]
                        }
                        */

                        offers.clear();

                        // Parse Offers array and load into array list view
                        for(int i = 0; i < offersArray.length(); i++) {
                            try {
                                JSONObject offerObj = offersArray.getJSONObject(i);

                                // Add Offer to Offers Array
                                Offer offer = new Offer(offerObj, context);
                                offers.add(offer);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // Notify offer adapter about data changes so that
                        // it renders the spinner view with updated data
                        offersAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(getString(R.string.Scubit_Add_Fragment) + ": Error : " + error.getMessage());
                    }
                });

        // Add the json request to volley request queue
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
        Log.d("Request Queue : ", requestQueue.toString());
        AppController.getInstance().addToRequestQueue(getBrandsBasedOnLocReq);
    }

    private void getPriceRanges() {

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + getString(R.string.get_scubits_context);

        // This api end point gives all the type of scubits price ranges that we have.
        apiEndPoint += "?q_case=2";

        Log.d(getString(R.string.Scubit_Add_Fragment), apiEndPoint);

        // Create Volley request to fetch the scubit price ranges
        JsonObjectRequest getBrandsBasedOnLocReq = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray priceRangeArray = new JSONArray();
                        try {
                            priceRangeArray = response.getJSONArray("price_ranges");
                            Log.d(getString(R.string.Scubit_Add_Fragment), priceRangeArray.toString());
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        /* Get price ranges endpoint response
                                   JSON-API format
                        {
                           price_ranges: [
                             {
                                  price_range_id: 1,
                                  price_range_name: "0-500"
                             },
                             {
                                  price_range_id: 2,
                                  price_range_name: "500-2000"
                             },
                             {
                                  price_range_id: 3,
                                  price_range_name: "2000-5000"
                             },
                             {
                                  price_range_id: 4,
                                  price_range_name: "> 5000"
                             }
                            ]
                         }
                        */

                        priceRanges.clear();

                        // Parse Price ranges array and load into array list view
                        for(int i = 0; i < priceRangeArray.length(); i++) {
                            try {
                                JSONObject priceRangeObj = priceRangeArray.getJSONObject(i);

                                // Add Price range to Price range Array
                                PriceRange priceRange = new PriceRange(priceRangeObj, context);
                                priceRanges.add(priceRange);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // Notify price range adapter about data changes so that
                        // it renders the spinner view with updated data
                        priceRangeAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(getString(R.string.Scubit_Add_Fragment) + ": Error : " + error.getMessage());
                    }
                });

        // Add the json request to volley request queue
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
        Log.d("Request Queue : ", requestQueue.toString());
        AppController.getInstance().addToRequestQueue(getBrandsBasedOnLocReq);
    }

    private void getPaymentTypes() {

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + getString(R.string.get_scubits_context);

        // This api end point gives all the type of scubits payment types that we have.
        apiEndPoint += "?q_case=3";

        Log.d(getString(R.string.Scubit_Add_Fragment), apiEndPoint);

        // Create Volley request to fetch the scubit offers
        JsonObjectRequest getBrandsBasedOnLocReq = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray paymentTypesArray = new JSONArray();
                        try {
                            paymentTypesArray = response.getJSONArray("payment_types");
                            Log.d(getString(R.string.Scubit_Add_Fragment), paymentTypesArray.toString());
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        /* Get payment types endpoint response
                                   JSON-API format
                        {
                             payment_types: [
                             {
                                 payment_id: 1,
                                 payment_name: "Cash"
                             },
                             {
                                 payment_id: 2,
                                 payment_name: "Debit Card"
                             },
                             {
                                 payment_id: 3,
                                 payment_name: "Credit Card"
                             }
                           ]
                        }
                        */

                        paymentTypes.clear();

                        // Parse payment types array and load into array list view
                        for(int i = 0; i < paymentTypesArray.length(); i++) {
                            try {
                                JSONObject paymentTypeObj = paymentTypesArray.getJSONObject(i);

                                // Add payment type to paymentTypes Array
                                PaymentType paymentType = new PaymentType(paymentTypeObj, context);
                                paymentTypes.add(paymentType);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // Notify payment type adapter about data changes so that
                        // it renders the spinner view with updated data
                        paymentTypeAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(getString(R.string.Scubit_Add_Fragment) + ": Error : " + error.getMessage());
                    }
                });

        // Add the json request to volley request queue
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
        Log.d("Request Queue : ", requestQueue.toString());
        AppController.getInstance().addToRequestQueue(getBrandsBasedOnLocReq);
    }

    private void getAllScubitContextValues() {

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + getString(R.string.get_scubits_context);

        // This api end point gives all the type of scubits offers, price ranges and payment types that we have.
        apiEndPoint += "?q_case=4";

        Log.d(getString(R.string.Scubit_Add_Fragment), apiEndPoint);

        // Create Volley request
        JsonObjectRequest getBrandsBasedOnLocReq = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray offersArray = new JSONArray();
                        JSONArray priceRangesArray = new JSONArray();
                        JSONArray paymentTypesArray = new JSONArray();
                        try {
                            offersArray = response.getJSONArray("offers");
                            priceRangesArray = response.getJSONArray("price_ranges");
                            paymentTypesArray = response.getJSONArray("payment_types");
                            Log.d(getString(R.string.Scubit_Add_Fragment), paymentTypesArray.toString());
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        /* Get payment types endpoint response
                                   JSON-API format
                        {
                           payment_types: [
                            {
                              payment_id: 1,
                              payment_name: "Cash"
                            },
                            {
                              payment_id: 2,
                              payment_name: "Debit Card"
                            }
                            ],
                            price_ranges: [
                              {
                                    price_range_id: 1,
                                    price_range_name: "0-500"
                             },
                             {
                                    price_range_id: 2,
                                    price_range_name: "500-2000"
                             }
                            ],
                            offers: [
                                {
                                   offer_id: 1,
                                   offer_name: "Buy 1 get 1 free"
                                },
                                {
                                    offer_id: 2,
                                    offer_name: "Buy 1 get 2nd 50% off"
                                }

                             ]
                        }
                        */

                        offers.clear();

                        // Parse Offers array and load into array list view
                        for(int i = 0; i < offersArray.length(); i++) {
                            try {
                                JSONObject offerObj = offersArray.getJSONObject(i);

                                // Add Offer to Offers Array
                                Offer offer = new Offer(offerObj, context);
                                offers.add(offer);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // Notify offer adapter about data changes so that
                        // it renders the spinner view with updated data
                        offersAdapter.notifyDataSetChanged();

                        priceRanges.clear();

                        // Parse Price ranges array and load into array list view
                        for(int i = 0; i < priceRangesArray.length(); i++) {
                            try {
                                JSONObject priceRangeObj = priceRangesArray.getJSONObject(i);

                                // Add Price range to Price range Array
                                PriceRange priceRange = new PriceRange(priceRangeObj, context);
                                priceRanges.add(priceRange);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // Notify price range adapter about data changes so that
                        // it renders the spinner view with updated data
                        priceRangeAdapter.notifyDataSetChanged();

                        paymentTypes.clear();

                        // Parse payment types array and load into array list view
                        for(int i = 0; i < paymentTypesArray.length(); i++) {
                            try {
                                JSONObject paymentTypeObj = paymentTypesArray.getJSONObject(i);

                                // Add payment type to paymentTypes Array
                                PaymentType paymentType = new PaymentType(paymentTypeObj, context);
                                paymentTypes.add(paymentType);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // Notify payment type adapter about data changes so that
                        // it renders the spinner view with updated data
                        paymentTypeAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(getString(R.string.Scubit_Add_Fragment) + ": Error : " + error.getMessage());
                    }
                });

        // Add the json request to volley request queue
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
        Log.d("Request Queue : ", requestQueue.toString());
        AppController.getInstance().addToRequestQueue(getBrandsBasedOnLocReq);
    }

    // Date time pickers
    void setupTimeAndDatePickers() {
        // Date and Time picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(c.getTime());
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        startDate = (EditText) thisView.findViewById(R.id.startDate);
        endDate = (EditText) thisView.findViewById(R.id.endDate);
        startTime = (EditText) thisView.findViewById(R.id.startTime);
        endTime = (EditText) thisView.findViewById(R.id.endTime);

        startDate.setText(month_name+" "+day+" "+year);
        endDate.setText(month_name + " " + day + " " + year);
        startTime.setText(TimePickerFragment.convert24To12HourFormat(hour, minute));
        // TODO : if +4 time falls into next day then update the day in date object
        endTime.setText(TimePickerFragment.convert24To12HourFormat((hour+4), minute));

        startDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                DialogFragment newFragment = DatePickerFragment.newInstance(arg0);
                newFragment.show(getActivity().getFragmentManager(), "startDatePicker");
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                DialogFragment newFragment = DatePickerFragment.newInstance(arg0);
                newFragment.show(getActivity().getFragmentManager(), "endDatePicker");
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                DialogFragment newFragment = TimePickerFragment.newInstance(arg0);
                newFragment.show(getActivity().getFragmentManager(), "startTimePicker");
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                DialogFragment newFragment = TimePickerFragment.newInstance(arg0);
                newFragment.show(getActivity().getFragmentManager(), "endTimePicker");
            }
        });
    }

    public void loadMallNames(){
        mallNamesSpinner = (Spinner) thisView.findViewById(R.id.mallNames);

        mallsAdapter = new ScubitSpinnerAdapter(context);
        mallNamesSpinner.setAdapter(mallsAdapter);
        String loc_id = GlobalUtils.getSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_location));


        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + getString(R.string.get_malls);

        // TODO : Based on actual user Location ID
        apiEndPoint += "?loc_id="+loc_id+"&q_case=1";

        Log.d(getString(R.string.Scubit_Add_Fragment), apiEndPoint);

        // Create Volley request to fetch the Malls based on location (city)
        JsonObjectRequest getMallsBasedOnLocReq = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray mallsArray = new JSONArray();
                        try {
                            mallsArray = response.getJSONArray("malls");
                            Log.d(getString(R.string.Scubit_Add_Fragment), mallsArray.toString());
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        /* Get malls endpoint response
                                   JSON-API format
                        {
                            malls: [
                                {
                                    mall_name: "Phoenix Market City",
                                    mall_id: 5,
                                    scubit_count: 45
                                },
                                {
                                    mall_name: "Garuda Mall",
                                    mall_id: 2,
                                    scubit_count: 30
                                },
                                {
                                    mall_name: "The Forum",
                                    mall_id: 1,
                                    scubit_count: 25
                                }
                            ]
                        }
                        */

                        // CLEAR BEFORE RELOADING
                        malls.clear();

                        // Parse Malls array and load into list view
                        for(int i = 0; i < mallsArray.length(); i++) {
                            try {
                                JSONObject mallObj = mallsArray.getJSONObject(i);

                                // Create a mall instance
                                Mall mall = new Mall(mallObj, context);

                                // Add Mall to Mall Array
                                malls.add(mall);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // Notify mall adapter about data changes so that
                        // it renders the list view with updated data
                        mallsAdapter.setList(malls, "malls");
                        mallsAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(getString(R.string.Scubit_Add_Fragment) + ": Error : " + error.getMessage());
                    }
                });

        // Add the json request to volley request queue
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
        Log.d("Request Queue : ", requestQueue.toString());
        AppController.getInstance().addToRequestQueue(getMallsBasedOnLocReq);

        // On item selected, load the appropriate shops
        mallNamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Fetch the mall object saved in the view
                Mall mall = (Mall) selectedItemView.getTag(R.id.spinnerItemText);
                loadShopNamesBasedOnMallId(mall);
                loadBrandNamesBasedOnMallId(mall);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    public void loadShopNamesBasedOnMallId(Mall mall) {
        shopNamesSpinner = (Spinner) thisView.findViewById(R.id.shopNames);
        shopProfileAdapter = new ScubitSpinnerAdapter(context);
        //shopProfileAdapter = new ShopProfileAdapter(this, context, shopProfiles, "mall", context.getResources().getInteger(R.integer.shop_spinner_item_mode));
        shopNamesSpinner.setAdapter(shopProfileAdapter);
        String loc_id = GlobalUtils.getSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_location));

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + getString(R.string.get_shops);

        // TODO : Based on actual user Location ID
        apiEndPoint += "?loc_id="+loc_id+"&q_case=" + getString(R.string.query_case_all_shops_by_mall_id) + "&mall_id=" + mall.getMallId();

        Log.d(getString(R.string.Scubit_Add_Fragment), apiEndPoint);

        // Create Volley request to fetch the Shops across all malls based on location (city)
        JsonObjectRequest getShopProfilesBasedOnMall = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(getString(R.string.ShopProfiles_Fragment), response.toString());
                        JSONArray shopProfileArray = new JSONArray();
                        try {
                            shopProfileArray = response.getJSONArray("shops");
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        /* Get shops endpoint response
                                   JSON-API format
                        {
                             shops: [
                             {
                               shop_name: "Adidas",
                               shop_profile_id: 1,
                               address: null,
                               floor: 3,
                               scubit_count: 11
                             }
                           ]
                        }
                        */

                        // CLEAR BEFORE RELOADING
                        shopProfiles.clear();

                        // Parse ShopProfiles array and load into list view
                        for(int i = 0; i < shopProfileArray.length(); i++) {
                            try {
                                JSONObject shopProfileObj = shopProfileArray.getJSONObject(i);

                                // Create a shop instance
                                ShopProfile shopProfile = new ShopProfile(shopProfileObj, context);

                                // Add Shop to Shop Array
                                shopProfiles.add(shopProfile);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // Notify shopprofile adapter about data changes so that
                        // it renders the list view with updated data
                        shopProfileAdapter.setList(shopProfiles, "shopProfiles");
                        shopProfileAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(getString(R.string.ShopProfiles_Fragment) + ": Error : " + error.getMessage());
                    }
                });

        // Add the json request to volley request queue
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
        Log.d("Request Queue : ", requestQueue.toString());
        AppController.getInstance().addToRequestQueue(getShopProfilesBasedOnMall);
    }

    public void loadBrandNamesBasedOnMallId(Mall mall) {
        brandNamesSpinner = (Spinner) thisView.findViewById(R.id.brandNames);
        //brandsAdapter = new BrandAdapter(context, brands, context.getResources().getInteger(R.integer.brand_spinner_item_mode));
        brandsAdapter = new ScubitSpinnerAdapter(context);
        brandNamesSpinner.setAdapter(brandsAdapter);
        String loc_id = GlobalUtils.getSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_location));

        // Form the end point
        String apiEndPoint = GlobalUtils.getDomain(context) + getString(R.string.get_brands);

        // TODO : Based on actual user Location ID.
        // TODO : Get All brands for now. Later should replaced with get all brands in mall
        apiEndPoint += "?loc_id="+loc_id+"&q_case=4";

        Log.d(getString(R.string.Scubit_Add_Fragment), apiEndPoint);

        // Create Volley request to fetch the Brands based on Mall id
        JsonObjectRequest getBrandsBasedOnMall = new JsonObjectRequest(apiEndPoint, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray brandsArray = new JSONArray();
                        try {
                            brandsArray = response.getJSONArray("brands");
                            Log.d(getString(R.string.Scubit_Add_Fragment), brandsArray.toString());
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        /* Get brands endpoint response
                                   JSON-API format
                        {
                            brands: [
                            {
                                brand_name: "Shoppers Stop",
                                brand_id: 4,
                                scubit_count: 52
                            },
                            {
                                brand_name: "Nike",
                                brand_id: 2,
                                scubit_count: 26
                            },
                            {
                                brand_name: "Reebok",
                                brand_id: 3,
                                scubit_count: 26
                            }
                            ]
                        }
                        */

                        // CLEAR BEFORE RELOADING
                        brands.clear();

                        // Parse Brands array and load into list view
                        for(int i = 0; i < brandsArray.length(); i++) {
                            try {
                                JSONObject mallObj = brandsArray.getJSONObject(i);

                                // Create a brand instance
                                Brand brand = new Brand(mallObj, context);

                                // Add Brand to Brands Array
                                brands.add(brand);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // Notify brand adapter about data changes so that
                        // it renders the list view with updated data
                        brandsAdapter.setList(brands, "brands");
                        brandsAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(getString(R.string.Scubit_Add_Fragment) + ": Error : " + error.getMessage());
                    }
                });

        // Add the json request to volley request queue
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
        Log.d("Request Queue : ", requestQueue.toString());
        AppController.getInstance().addToRequestQueue(getBrandsBasedOnMall);
    }

    private JSONObject validateAndGetScubitObject() {
        // Required parameters
        // shop_profile_id
        View shopProfileView = shopNamesSpinner.getSelectedView();
        ShopProfile shopProfile = (ShopProfile) shopProfileView.getTag(R.id.spinnerItemText);
        int shop_profile_id = shopProfile.getShopProfileId();

        // If sessions exists, then fetch user id. user_id
        Integer user_id = 2;
        if (GlobalUtils.containsSharedPreference(context, getString(R.string.sp_user), getString(R.string.user_session))) {
            user_id = GlobalUtils.getIntSharedPreference(context, getString(R.string.sp_user), getString(R.string.scube_id));
        } else {
            navigationController.navigateToLogin("false");
        }

        // gender_id
        Integer gender_id = 1;

        // offer_id
        View offerView = offersSpinner.getSelectedView();
        Offer offer = (Offer) offerView.getTag(R.id.spinnerItemText);
        int offer_id = offer.getOfferId();

        // price_range_id
        View priceRangeView = priceRangesSpinner.getSelectedView();
        PriceRange priceRange = (PriceRange) priceRangeView.getTag(R.id.spinnerItemText);
        int price_range_id = priceRange.getPriceRangeId();

        // payment_type_id
        View paymentTypeView = paymentTypesSpinner.getSelectedView();
        PaymentType paymentType = (PaymentType) paymentTypeView.getTag(R.id.spinnerItemText);
        int payment_type_id = paymentType.getPaymentTypeId();

        String[] dateParts, timeParts;
        // start_date_time : YYYY-MM-DD HH-MM-SS
        dateParts = startDate.getText().toString().split(" ");
        timeParts = startTime.getText().toString().split(" ");
        timeParts = timeParts[0].split(":");
        String start_date_time = dateParts[2]+"-"+DatePickerFragment.convertMonthToInt(dateParts[0])+"-"+dateParts[1]+" ";
        start_date_time += timeParts[0]+"-"+timeParts[1]+"-00";

        // end_date_time : YYYY-MM-DD HH-MM-SS
        dateParts = endDate.getText().toString().split(" ");
        timeParts = endTime.getText().toString().split(" ");
        timeParts = timeParts[0].split(":");
        String end_date_time = dateParts[2]+"-"+DatePickerFragment.convertMonthToInt(dateParts[0])+"-"+dateParts[1]+" ";
        end_date_time += timeParts[0]+"-"+timeParts[1]+"-00";

        // brand_id
        View brandView = brandNamesSpinner.getSelectedView();
        Brand brand = (Brand) brandView.getTag(R.id.spinnerItemText);
        int brand_id = brand.getBrandId();

        // other_brand_name
        String other_brand_name = "";

        // notes
        String notes = "";

        // photo_url
        String photo_url = "";

        // number_of_items
        //int number_of_items =

        // scubit_id
        //int scubit_id =

        // Create POST JSON object
        JSONObject addScubitObject = new JSONObject();
        try {
            addScubitObject.put("action_type", "1");
            addScubitObject.put("shop_profile_id", shop_profile_id);
            addScubitObject.put("user_id", user_id);
            addScubitObject.put("gender_id", gender_id);
            addScubitObject.put("offer_id", offer_id);
            addScubitObject.put("price_range_id", price_range_id);
            addScubitObject.put("payment_type_id", payment_type_id);
            addScubitObject.put("start_date_time", start_date_time);
            addScubitObject.put("end_date_time", end_date_time);
            addScubitObject.put("brand_id", brand_id);
            addScubitObject.put("other_brand_name", JSONObject.NULL);
            addScubitObject.put("notes", JSONObject.NULL);
            addScubitObject.put("photo_url", JSONObject.NULL);
            addScubitObject.put("number_of_items", JSONObject.NULL);
            addScubitObject.put("scubit_id", JSONObject.NULL);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(getString(R.string.Scubit_Add_Fragment), addScubitObject.toString());

        return addScubitObject;
    }
}

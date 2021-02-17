package com.technova.help.CountryCode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.technova.help.DataObject;
import com.technova.help.MyRecyclerViewAdapter;
import com.technova.help.R;

import java.util.ArrayList;

public class CountryCodeActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String[] gotCNames = {
            "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra ", "Angola ", "Anguilla ", "Antarctica", "Antigua", "Argentina", "Armenia ", "Aruba", "Ascension", "Australia ", "Australian External Territories", "Austria", "Azerbaijan",
            "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Barbuda", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia ", "Bosnia & Herzegovina ", "Botswana ", "Brazil ", "British Virgin Islands", "Brunei Darussalam", "Bulgaria", "Burkina Faso ", "Burundi",
            "Cambodia", "Cameroon", "Canada", "Cape Verde Islands", "Cayman Islands", "Central African Republic", "Chad ", "Chatham Island (New Zealand)", "Chile ", "China", "Christmas Island", "Cocos-Keeling Islands", "Colombia ", "Comoros", "Congo", "Congo, Dem. Rep. of  (former Zaire) ", "Cook Islands", "Costa Rica", "Côte d'Ivoire (Ivory Coast)", "Croatia", "Cuba", "Cuba (Guantanamo Bay)", "Curaçao", "Cyprus", "Czech Republic",
            "Denmark", "Diego Garcia", "Djibouti", "Dominica", "Dominican Republic ",
            "East Timor", "Easter Island", "Ecuador ", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia",
            "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji Islands", "Finland", "France", "French Antilles", "French Guiana", "French Polynesia",
            "Gabonese Republic", "Gambia", "Georgia", "Germany", "Ghana ", "Gibraltar ", "Greece ", "Greenland ", "Grenada", "Guadeloupe", "Guam", "Guantanamo Bay", "Guatemala ", "Guinea-Bissau ", "Guinea", "Guyana",
            "Haiti ", "Honduras", "Hong Kong", "Hungary ",
            "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel ", "Italy ",
            "Jamaica ", "Japan ", "Jordan",
            "Kazakhstan", "Kenya", "Kiribati ", "Korea (North)", "Korea (South)", "Kuwait ", "Kyrgyz Republic",
            "Laos", "Latvia ", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania ", "Luxembourg",
            "Macao", "Macedonia (Former Yugoslav Rep of.)", "Madagascar", "Malawi ", "Malaysia", "Maldives", "Mali Republic", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte Island", "Mexico", "Micronesia, (Federal States of)", "Midway Island", "Moldova ", "Monaco", "Mongolia ", "Montenegro", "Montserrat ", "Morocco", "Mozambique", "Myanmar",
            "Namibia", "Nauru", "Nepal ", "Netherlands", "Netherlands Antilles", "Nevis", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Marianas Islands (Saipan, Rota, & Tinian)",
            "Oman",
            "Pakistan", "Palau", "Palestinian Settlements", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Puerto Rico",
            "Qatar",
            "Réunion Island", "Romania", "Russia", "Rwandese Republic",
            "St. Helena", "St. Kitts/Nevis", "St. Lucia", "St. Pierre & Miquelon", "St. Vincent & Grenadines", "Samoa", "San Marino", "São Tomé and Principe", "Saudi Arabia", "Senegal ", "Serbia", "Seychelles Republic", "Sierra Leone", "Singapore", "Slovak Republic", "Slovenia ", "Solomon Islands", "Somali Democratic Republic", "South Africa", "Spain", "Sri Lanka", "Sudan", "Suriname ", "Swaziland", "Sweden", "Switzerland", "Syria",
            "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor Leste", "Togolese Republic", "Tokelau ", "Tonga Islands", "Trinidad & Tobago", "Tunisia", "Turkey", "Turkmenistan ", "Turks and Caicos Islands", "Tuvalu",
            "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States of America", "US Virgin Islands", "Uruguay", "Uzbekistan",
            "Vanuatu", "Venezuela", "Vietnam",
            "Wake Island", "Wallis and Futuna Islands",
            "Yemen",
            "Zambia ", "Zanzibar", "Zimbabwe ",};
    String[] gotCNos = {};
    int gotCCount=gotCNames.length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_code);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerAllContacts);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

    }


    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        for (int index = 0; index < gotCCount; index++) {
            DataObject obj = new DataObject(gotCNames[index], gotCNos[index]);
            results.add(index, obj);
        }

        return results;
    }

}

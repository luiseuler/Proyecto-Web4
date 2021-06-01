package com.lagg.enfriamiento;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    RecyclerView rvMsg;
    SharedPreferences sesion;
    String lista[][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        rvMsg = findViewById(R.id.rvMsg);

        sesion = getSharedPreferences("sesion", 0);
        getSupportActionBar().setTitle("Usuario : " + sesion.getString("user", ""));

        rvMsg.setHasFixedSize(true);
        rvMsg.setItemAnimator(new DefaultItemAnimator());
        rvMsg.setLayoutManager(new LinearLayoutManager(this));

        llenar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater  = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.id){
            llenar();
        }
        return super.onOptionsItemSelected(item);
    }

    private void llenar() {
        String url = Uri.parse(Config.URL + "sensor.php")
                .buildUpon()
                .build().toString();
        System.out.println(url);
        JsonArrayRequest peticion = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        llenarRespuesta(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity2.this, "Error de red", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization", sesion.getString("token","Error"));
                return header;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(peticion);
    }

    private void llenarRespuesta(JSONArray response) {
        try{
            lista = new String[response.length()][5];
            for (int i = 0; i < response.length(); i++) {
                lista[i][0] = response.getJSONObject(i).getString("id");
                lista[i][1] = response.getJSONObject(i).getString("user");
                //lista[i][2] = response.getJSONObject(i).getString("tipo");
                lista[i][3] = response.getJSONObject(i).getString("valor");
                lista[i][4] = response.getJSONObject(i).getString("hora");
            }

            rvMsg.setAdapter(new MiAdapter(lista, new RecyclerViewOnItemClickListener() {
                @Override
                public void onClick(View v, int position) {

                }

                @Override
                public void onClickDel(View v, int position) {
                    new AlertDialog.Builder(MainActivity2.this)
                            .setTitle("Eliminar")
                            .setMessage("Quieres eliminar el mensaje id=" +  lista[position][0] + "?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    eliminar(lista[position][0]);
                                }
                            })
                            .setNegativeButton("No",null)
                            .create().show();
                }
            }));
            Toast.makeText(MainActivity2.this, "Lista actualida", Toast.LENGTH_SHORT).show();
        }catch (Exception e){}
    }

    private void eliminar(String id) {
        String url = Uri.parse(Config.URL + "sensor.php")
                .buildUpon()
                .appendQueryParameter("id", id)
                .build().toString();
        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        respuestaEliminar(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity2.this, "Error de red", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", sesion.getString("token", "Error"));
                return header;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(peticion);
    }

    private void respuestaEliminar(JSONObject response) {
        try{
            if (response.getString("del").compareTo("y") == 0){
                Toast.makeText(this, "Datos eliminador", Toast.LENGTH_SHORT).show();
                llenar();
            }else{
                Toast.makeText(this, "No se puede eliminar", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}
    }
}
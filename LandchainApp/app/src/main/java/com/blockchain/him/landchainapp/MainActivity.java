package com.blockchain.him.landchainapp;

import android.Manifest;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    Web3j  web3j;
    Credentials credentials;
    LandRegistry landRegistry;
    String walletAddress;
    Task task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        task = new Task();
                        task.execute();
                    } else {
                        Toast.makeText(this, "Permissions needed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class Task extends AsyncTask<Void,Void,Void>{

        @Override

        protected Void doInBackground(Void... voids) {
            web3j= Web3jFactory.build(new HttpService("https://rinkeby.infura.io/uv2A4DBLeYIY3ZnFmH3C"));
            try {
                Log.d("web3j", "Version" + web3j.web3ClientVersion().send().getWeb3ClientVersion());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                credentials = WalletUtils.loadCredentials("happynewyear",
                        "/sdcard/Download/UTC--2018-07-06T19-31-33.617987500Z--518a9531272441bd625c8d7db813d4f1751d81ba.json");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CipherException e) {
                e.printStackTrace();
            }

            walletAddress = credentials.getAddress();
            Log.d("web3j", "walletAddress: "+walletAddress);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            BigInteger id = new BigInteger("1");
            loadContract(id);
        }
    }

    public void loadContract(BigInteger id){
        landRegistry = LandRegistry.load("0x66f2097e3b1e2f2fdeb2cd834a276f55e64a870e",web3j,credentials, Contract.GAS_PRICE,
                Contract.GAS_LIMIT);
        try {
            Log.d("web3j", "loadContract: "+landRegistry.getLandData(id).sendAsync().get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

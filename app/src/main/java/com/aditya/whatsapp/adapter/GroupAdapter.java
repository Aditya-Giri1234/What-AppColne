package com.aditya.whatsapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aditya.whatsapp.R;
import com.aditya.whatsapp.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GroupAdapter  extends RecyclerView.Adapter{


    ArrayList<MessageModel> messageModels;
    Context context;

    int SENDER_VIEW_TYPE=1;
    int RECEIVER_VIEW_TYPE=2;

    public GroupAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==SENDER_VIEW_TYPE){
            View view= LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return  new GroupAdapter.SenderViewHolder(view);
        }

        else{
            View view= LayoutInflater.from(context).inflate(R.layout.sample_receiver,parent,false);
            return  new GroupAdapter.ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {

        System.out.println(messageModels.get(0)+"In group");
        if(messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }
        else
            return RECEIVER_VIEW_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel=messageModels.get(position);

        holder.itemView.setOnLongClickListener(view->{

            if(FirebaseAuth.getInstance().getUid().equals(messageModel.getuId())) {

                new AlertDialog.Builder(context).setTitle("Delete").setMessage("Are you sure you want to delete this message ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        System.out.println(messageModel.getMessageId());
                        database.getReference().child("Groups").child(messageModel.getMessageId()).removeValue();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
            else{
                new AlertDialog.Builder(context).setTitle("Info").setMessage("You can't delete other message now !").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
            return  true;
        });

        if(holder.getClass()== GroupAdapter.SenderViewHolder.class){
            ((GroupAdapter.SenderViewHolder)holder).senderText.setText(messageModel.getMessage());
        }
        else{
            ((GroupAdapter.ReceiverViewHolder)holder).receiverText.setText(messageModel.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public  class ReceiverViewHolder extends RecyclerView.ViewHolder{

        TextView receiverText,receiverTime;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverText=itemView.findViewById(R.id.receiverText);
            receiverTime=itemView.findViewById(R.id.receiverTime);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView senderText,senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderText=itemView.findViewById(R.id.senderText);
            senderTime=itemView.findViewById(R.id.senderTime);
        }
    }
}

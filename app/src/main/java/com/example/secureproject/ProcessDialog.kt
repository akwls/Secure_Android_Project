package com.example.secureproject

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.lang.IllegalStateException

class ProcessDialog(var alphabetBoard: Array<CharArray>, var playFair: ArrayList<CharArray>, var encrypted: ArrayList<CharArray>): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view:View = inflater.inflate(R.layout.dialog_process, null)

            var alphabetGrid = view.findViewById<GridLayout>(R.id.alphabet_grid)
            var mappingPlainTxt = view.findViewById<TextView>(R.id.mapping_plain_txt)
            var replacedEncryptTxt = view.findViewById<TextView>(R.id.replaced_encrypt_txt)

            var mappingPlain: String = ""
            var replacedEncrypt: String = ""

            for(i in alphabetBoard.indices) {
                for(j in alphabetBoard[i].indices) {
                    val txt = TextView(context)
                    if(alphabetBoard[i][j].toString().equals("q")) {
                        txt.text = alphabetBoard[i][j].toString() + "/z"
                    }
                    else txt.text = alphabetBoard[i][j].toString()
                    val lp = LinearLayout.LayoutParams(100, 100)
                    txt.layoutParams = lp
                    txt.textSize = 20.0f
                    txt.gravity = Gravity.CENTER
                    txt.setBackgroundResource(R.drawable.background_process_table)

                    alphabetGrid.addView(txt)

                }
            }

            for(i in 0 until playFair.size) {
                mappingPlain += playFair.get(i)[0] + "" + playFair.get(i)[1] + " "
            }

            for(i in 0 until encrypted.size) {
                replacedEncrypt += encrypted.get(i)[0] + "" + encrypted.get(i)[1] + " "
            }

            mappingPlainTxt.text = mappingPlain
            replacedEncryptTxt.text = replacedEncrypt


            builder.setView(view)
                .setPositiveButton("확인", DialogInterface.OnClickListener{ dialog, id
                    -> getDialog()?.cancel()
                })


            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}
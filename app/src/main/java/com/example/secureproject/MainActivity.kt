package com.example.secureproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.secureproject.database.Encrypt
import com.example.secureproject.database.EncryptDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var edtKey: EditText
    lateinit var plainText: EditText

    lateinit var encryptText: TextView
    lateinit var decryptionText: TextView

    lateinit var txtEncryptText: TextView
    lateinit var txtDecryptionText: TextView

    lateinit var btnReset: Button
    lateinit var btnProcess: Button
    lateinit var btnEncrypt: Button
    lateinit var btnDecrypt: Button

    lateinit var btnRecent: Button
    lateinit var btnDesc: Button

    lateinit var playFair: ArrayList<CharArray>
    lateinit var encPlayFair: ArrayList<CharArray>
    lateinit var decPlayFair: ArrayList<CharArray>

    lateinit var db: EncryptDatabase

    var alphabetBoard = Array(5,{CharArray(5)})
    var oddFlag = false
    var zCheck = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "3104"

        db = EncryptDatabase.getInstance(this)

        var blankCheck = ""

        edtKey = findViewById(R.id.edt_key)
        plainText = findViewById(R.id.plain_text)
        encryptText = findViewById(R.id.encrypt_text)
        decryptionText = findViewById(R.id.decryption_text)

        txtEncryptText = findViewById(R.id.txt_encrypt_text)
        txtDecryptionText = findViewById(R.id.txt_decryption_text)

        btnReset = findViewById(R.id.btn_reset)
        btnProcess = findViewById(R.id.btn_process)
        btnEncrypt = findViewById(R.id.btn_encrypt)
        btnDecrypt = findViewById(R.id.btn_decrypt)

        btnRecent = findViewById(R.id.btn_recent)
        btnDesc = findViewById(R.id.btn_desc)

        var encrypted = ""
        var plainStr = ""
        var keyStr = ""
        var decrypted = ""

        btnEncrypt.setOnClickListener {
            keyStr = edtKey.text.toString().trim()
            plainStr = plainText.text.toString().trim()

            if(keyStr.isNotBlank() && plainStr.isNotBlank()) {
                setBoard(keyStr)
                var i:Int =0
                while(i<plainStr.length) {
                    if (plainStr[i] == ' ') //공백제거
                    {
                        plainStr = plainStr.substring(0, i) + plainStr.substring(i + 1, plainStr.length)
                        blankCheck += 10
                    } else {
                        blankCheck += 0
                    }
                    if (plainStr[i] === 'z') //z를 q로 바꿔줘서 처리함.
                    {
                        plainStr = plainStr.substring(0, i) + 'q' + plainStr.substring(i + 1, plainStr.length)
                        zCheck += 1
                    } else {
                        zCheck += 0
                    }
                    i++
                }
                Log.d("blankCheck", blankCheck)

                encrypted = encrypt(keyStr, plainStr);

                txtEncryptText.visibility = View.VISIBLE
                encryptText.visibility = View.VISIBLE

                btnProcess.isEnabled = true
                btnEncrypt.visibility = View.GONE
                btnDecrypt.visibility = View.VISIBLE


                encryptText.text = encrypted

                CoroutineScope(Dispatchers.Main).launch {
                    async(Dispatchers.Default) {
                        db.encryptDAO().insert(Encrypt(plainText.text.toString().trim(), encrypted))
                    }.await()
                }
            }
            else {
                Toast.makeText(applicationContext, "암호키와 평문을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }



        }

        btnDecrypt.setOnClickListener {
            txtDecryptionText.visibility = View.VISIBLE
            decryptionText.visibility = View.VISIBLE

            btnDecrypt.isEnabled = false

            var idx = 0
            while(idx < encrypted.length) {
                if(encrypted[idx] == ' ') {
                    encrypted = encrypted.substring(0, idx)+encrypted.substring(idx+1, encrypted.length)
                }
                idx++
            }

            Log.d("encrypted", encrypted)
            decrypted = decrypt(keyStr, encrypted, zCheck)
            Log.d("decrypted", decrypted)

            idx = 0
            while(idx < decrypted.length) {
                if(blankCheck[idx] == '1') {
                    decrypted = decrypted.substring(0, idx) + " " + decrypted.substring(idx, decrypted.length)
                }
                idx++
            }

            decryptionText.text = decrypted

        }

        btnProcess.setOnClickListener {
            val dialog = ProcessDialog(alphabetBoard, playFair, encPlayFair)
            dialog.show(
                supportFragmentManager, "암호화 과정 보기"
            )
        }

        btnReset.setOnClickListener {
            edtKey.setText("")
            plainText.setText("")

            txtEncryptText.visibility = View.GONE
            encryptText.text = ""
            encryptText.visibility = View.GONE
            txtDecryptionText.visibility = View.GONE
            decryptionText.text = ""
            decryptionText.visibility = View.GONE

            btnProcess.isEnabled = false
            btnDecrypt.visibility = View.GONE
            btnEncrypt.visibility = View.VISIBLE

            edtKey.requestFocus()


        }

        btnRecent.setOnClickListener {
            startActivity(Intent(this, RecentActivity::class.java))
        }

        btnDesc.setOnClickListener {
            startActivity(Intent(this, DescActivity::class.java))
        }

    }
    fun encrypt(keyStr:String, plainStr:String) : String {
        playFair = ArrayList<CharArray>()
        encPlayFair = ArrayList<CharArray>()
        var x1 = 0
        var x2 = 0
        var y1 = 0
        var y2 = 0
        var encStr = ""

        var i: Int = 0

        while(i < plainStr.length) {
            var tmpArr = CharArray(2)
            tmpArr[0] = plainStr[i]
            try {
                if(plainStr[i] == plainStr[i+1]) {
                    tmpArr[1] = 'x'
                    i--
                }
                else {
                    tmpArr[1] = plainStr[i+1]
                }
            }catch (e : StringIndexOutOfBoundsException ) {
                tmpArr[1] = 'x'
                oddFlag = true;
            }
            playFair.add(tmpArr)
            i+=2
        }

        var encryptResult = "";

        for(i in 0 until playFair.size) {
            var tmpArr = CharArray(2)
            for(j in 0 until alphabetBoard.size) {
                for(k in 0 until alphabetBoard[j].size) {
                    if(alphabetBoard[j][k] == playFair.get(i)[0]) {
                        x1 = j
                        y1 = k
                    }
                    if(alphabetBoard[j][k] == playFair.get(i)[1]) {
                        x2 = j
                        y2 = k
                    }
                }
            }

            if(x1 == x2) {
                tmpArr[0] = alphabetBoard[x1][(y1+1)%5]
                tmpArr[1] = alphabetBoard[x2][(y2+1)%5]
            }
            else if(y1 == y2) {
                tmpArr[0] = alphabetBoard[(x1+1)%5][y1]
                tmpArr[1] = alphabetBoard[(x2+1)%5][y2]
            }
            else {
                tmpArr[0] = alphabetBoard[x2][y1];
                tmpArr[1] = alphabetBoard[x1][y2];
            }
            encPlayFair.add(tmpArr);
        }
        for(i in 0 until encPlayFair.size) {
            encryptResult += encPlayFair.get(i)[0] + "" + encPlayFair.get(i)[1] + " "
        }

        return encryptResult

    }

    fun decrypt(keyStr: String, encrypted: String, zCheck: String) : String {
        decPlayFair = ArrayList<CharArray>()
        playFair = ArrayList<CharArray>()
        var x1 = 0;
        var x2 = 0
        var y1 = 0
        var y2 = 0
        var decStr = ""

        var lengthOddFlag = 1

        for(i in 0 until encrypted.length step 2) {
            var tmpArr = CharArray(2)
            tmpArr[0] = encrypted[i]
            tmpArr[1] = encrypted[i+1]
            playFair.add(tmpArr)
        }

        for(i in 0 until playFair.size) {
            var tmpArr = CharArray(2)
            for(j in 0 until alphabetBoard.size) {
                for(k in 0 until alphabetBoard[j].size) {
                    if(alphabetBoard[j][k] == playFair[i][0]) {
                        x1 = j
                        y1 = k
                    }
                    if(alphabetBoard[j][k] == playFair.get(i)[1]) {
                        x2 = j
                        y2 = k
                    }
                }
            }
            if(x1 == x2) {
                tmpArr[0] = alphabetBoard[x1][(y1+4)%5]
                tmpArr[1] = alphabetBoard[x2][(y2+4)%5]
            }
            else if(y1 == y2) {
                tmpArr[0] = alphabetBoard[(x1+4)%5][y1]
                tmpArr[1] = alphabetBoard[(x2+4)%5][y2]
            }
            else {
                tmpArr[0] = alphabetBoard[x2][y1]
                tmpArr[1] = alphabetBoard[x1][y2]
            }
            decPlayFair.add(tmpArr)
        }

        for(i in 0 until decPlayFair.size) {
            if(i != decPlayFair.size -1 && decPlayFair.get(i)[1] == 'x' && decPlayFair.get(i)[0] == decPlayFair.get(i+1)[0]) {
                decStr += decPlayFair.get(i)[0]
            }
            else {
                decStr += decPlayFair.get(i)[0] + "" + decPlayFair.get(i)[1]
            }
        }

        for(i in 0 until zCheck.length) {
            if(zCheck[i] == '1') {
                decStr = decStr.substring(0, i) + 'z' + decStr.substring(i+1, decStr.length)
            }
        }

        if(oddFlag) decStr = decStr.substring(0, decStr.length-1)

        return decStr
    }

    fun setBoard(keyStr: String) {
        var key = keyStr
        var keyForSet = ""
        var duplicationFlag = false
        var keyLengthCount = 0

        key += "abcdefghijklmnopqrstuvwxyz";

        for (i in 0 until key.length) {
            for (j in 0 until keyForSet.length) {
                if (key[i] == keyForSet[j]) {
                    duplicationFlag = true
                    break
                }
            }
            if (!duplicationFlag) keyForSet += key[i]
            duplicationFlag = false
        }

        for (i in 0 until alphabetBoard.size) {
            for (j in 0 until alphabetBoard[i].size) {
                alphabetBoard[i][j] = keyForSet[keyLengthCount++]
            }
        }
    }
}
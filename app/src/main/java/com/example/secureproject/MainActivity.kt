package com.example.secureproject

import android.content.Intent
import android.os.Bundle
import android.text.InputType
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
    var blankCheck = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "3104"

        // 데이터베이스 연결
        db = EncryptDatabase.getInstance(this)

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

        edtKey.requestFocus()

        // "암호화" 버튼 클릭 이벤트
        btnEncrypt.setOnClickListener {
            keyStr = edtKey.text.toString().trim() // 암호키
            plainStr = plainText.text.toString().trim() // 평문

            // 입력값이 비어있지 않은 경우
            if(keyStr.isNotBlank() && plainStr.isNotBlank()) {
                // 암호판 생성
                setBoard(keyStr)
                var i:Int =0
                while(i<plainStr.length) {
                    if (plainStr[i] == ' ') // 공백 제거
                    {
                        plainStr = plainStr.substring(0, i) + plainStr.substring(i + 1, plainStr.length)
                        blankCheck += 10 // 띄어쓰기 체크
                    } else {
                        blankCheck += 0
                    }
                    if (plainStr[i] === 'z') // z를 q로 바꿔줘서 처리함.
                    {
                        plainStr = plainStr.substring(0, i) + 'q' + plainStr.substring(i + 1, plainStr.length)
                        zCheck += 1 // z 체크
                    } else {
                        zCheck += 0
                    }
                    i++
                }

                // 암호화
                encrypted = encrypt(keyStr, plainStr);

                txtEncryptText.visibility = View.VISIBLE
                encryptText.visibility = View.VISIBLE

                btnProcess.isEnabled = true
                btnEncrypt.visibility = View.GONE
                btnDecrypt.visibility = View.VISIBLE


                encryptText.text = encrypted

                // 평문, 암호문 데이터베이스에 삽입
                CoroutineScope(Dispatchers.Main).launch {
                    async(Dispatchers.Default) {
                        db.encryptDAO().insert(Encrypt(keyStr, plainText.text.toString().trim(), encrypted))
                    }.await()
                }
                edtKey.inputType = InputType.TYPE_NULL
                plainText.inputType = InputType.TYPE_NULL

            }
            // 입력값이 공백인 경우
            else {
                Toast.makeText(applicationContext, "암호키와 평문을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // "복호화" 버튼
        btnDecrypt.setOnClickListener {
            txtDecryptionText.visibility = View.VISIBLE
            decryptionText.visibility = View.VISIBLE

            btnDecrypt.isEnabled = false

            var idx = 0
            // 암호문 공백 제거
            while(idx < encrypted.length) {
                if(encrypted[idx] == ' ') {
                    encrypted = encrypted.substring(0, idx)+encrypted.substring(idx+1, encrypted.length)
                }
                idx++
            }

            // 복호화
            decrypted = decrypt(keyStr, encrypted, zCheck)

            idx = 0
            // 복호문에 띄어쓰기 추가
            while(idx < decrypted.length) {
                if(blankCheck[idx] == '1') {
                    decrypted = decrypted.substring(0, idx) + " " + decrypted.substring(idx, decrypted.length)
                }
                idx++
            }

            decryptionText.text = decrypted

        }

        // "과정 보기" 버튼
        btnProcess.setOnClickListener {
            // 대화창 띄우기
            val dialog = ProcessDialog(alphabetBoard, playFair, encPlayFair)
            dialog.show(
                supportFragmentManager, "암호화 과정 보기"
            )
        }

        // "초기화" 버튼
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
            btnDecrypt.isEnabled = true

            edtKey.inputType = InputType.TYPE_CLASS_TEXT
            plainText.inputType = InputType.TYPE_CLASS_TEXT

            edtKey.requestFocus()
        }

        // "최근 내역" 버튼
        btnRecent.setOnClickListener {
            startActivity(Intent(this, RecentActivity::class.java))
        }

        // "다중 문자 치환이란?" 버튼
        btnDesc.setOnClickListener {
            startActivity(Intent(this, DescActivity::class.java))
        }

    }

    // 암호화 메서드
    fun encrypt(keyStr:String, plainStr:String) : String {
        playFair = ArrayList<CharArray>() // 평문 매핑
        encPlayFair = ArrayList<CharArray>() // 암호문 매핑
        var x1 = 0
        var x2 = 0
        var y1 = 0
        var y2 = 0
        var encStr = ""

        var i: Int = 0

        // 평문 매핑
        while(i < plainStr.length) {
            var tmpArr = CharArray(2)
            tmpArr[0] = plainStr[i]
            try {
                // 앞뒤 글자가 같을 경우 x 추가
                if(plainStr[i] == plainStr[i+1]) {
                    tmpArr[1] = 'x'
                    i--
                }
                else {
                    tmpArr[1] = plainStr[i+1]
                }
            }catch (e : StringIndexOutOfBoundsException ) { // 맨 마지막 매핑 쌍이 한글자일 경우 마지막에 x 추가
                tmpArr[1] = 'x'
                oddFlag = true;
            }
            playFair.add(tmpArr)
            i+=2
        }

        var encryptResult = "";

        // 암호문 매핑
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

            if(x1 == x2) { // 같은 열에 있을 경우 아래쪽에 있는 문자로 치환
                tmpArr[0] = alphabetBoard[x1][(y1+1)%5]
                tmpArr[1] = alphabetBoard[x2][(y2+1)%5]
            }
            else if(y1 == y2) { // 같은 행에 있을 경우 오른쪽에 있는 문자로 치환
                tmpArr[0] = alphabetBoard[(x1+1)%5][y1]
                tmpArr[1] = alphabetBoard[(x2+1)%5][y2]
            }
            else { // 둘 다 아닐 경우
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

    // 복호화 메서드
    fun decrypt(keyStr: String, encrypted: String, zCheck: String) : String {
        decPlayFair = ArrayList<CharArray>() // 복호문 매핑
        playFair = ArrayList<CharArray>() // 암호문 매핑
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

        // 복호문 매핑
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
            if(x1 == x2) { // 같은 열에 있을 경우 위에 있는 문자로 치환
                tmpArr[0] = alphabetBoard[x1][(y1+4)%5]
                tmpArr[1] = alphabetBoard[x2][(y2+4)%5]
            }
            else if(y1 == y2) { // 같은 행에 있을 경우 왼쪽에 있는 문자로 치환
                tmpArr[0] = alphabetBoard[(x1+4)%5][y1]
                tmpArr[1] = alphabetBoard[(x2+4)%5][y2]
            }
            else { // 둘 다 아닐 경우
                tmpArr[0] = alphabetBoard[x2][y1]
                tmpArr[1] = alphabetBoard[x1][y2]
            }
            decPlayFair.add(tmpArr)
        }

        // 평문에서 같은 문자가 연속으로 나왔는지 체크
        // 예) 복호문 매핑 결과가 sx sa 로 나왔을 경우, 평문은 ssa임.
        for(i in 0 until decPlayFair.size) {
            if(i != decPlayFair.size -1 && decPlayFair.get(i)[1] == 'x' && decPlayFair.get(i)[0] == decPlayFair.get(i+1)[0]) {
                decStr += decPlayFair.get(i)[0] // 그럴 경우 앞 문자만 넣기
            }
            else {
                // 아닐 경우 두 글자 모두 넣기
                decStr += decPlayFair.get(i)[0] + "" + decPlayFair.get(i)[1]
            }
        }

        // 평문에서 z가 등장하는 위치에 z 삽입.
        for(i in 0 until zCheck.length) {
            if(zCheck[i] == '1') {
                decStr = decStr.substring(0, i) + 'z' + decStr.substring(i+1, decStr.length)
            }
        }

        // 평문의 글자 수가 홀수일 경우 마지막 글자 제거
        if(oddFlag) decStr = decStr.substring(0, decStr.length-1)

        return decStr
    }

    // 암호판 생성 메서드
    fun setBoard(keyStr: String) {
        var key = keyStr
        var keyForSet = ""
        var duplicationFlag = false
        var keyLengthCount = 0

        // 암호키 뒤에 연속된 알파벳 연결
        key += "abcdefghijklmnopqrstuvwxyz";

        // 암호키를 돌며 암호판에 삽입
        for (i in 0 until key.length) {
            // i 인덱스의 알파벳이 이미 암호판에 있는지 체크
            for (j in 0 until keyForSet.length) {
                if (key[i] == keyForSet[j]) {
                    duplicationFlag = true
                    break
                }
            }
            // i 인덱스의 알파벳이 암호판에 없다면 삽입
            if (!duplicationFlag) keyForSet += key[i]
            duplicationFlag = false
        }

        // 암호판을 2차원 배열로 생성
        for (i in 0 until alphabetBoard.size) {
            for (j in 0 until alphabetBoard[i].size) {
                alphabetBoard[i][j] = keyForSet[keyLengthCount++]
            }
        }
    }
}
package com.example.vilmarferreira.opengltextura;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


class Renderizador implements GLSurfaceView.Renderer
{

    int iFPS;
    long tempoInicial=0;
    long tempoAtual=0;
    float PosX=000,PosY=000;
    float PosLargura,PosAltura;
    int dir=1,dir2=1;
    float angulo=1;
    int lado=200,H=600;
    private Context vrActivity;


    public Renderizador (Context vrActivity)
    {
        this.vrActivity= vrActivity;
    }
    //será chamado quando o aplicativo for criado, 1 vez só
    public void onSurfaceCreated(GL10 vrOpengl, EGLConfig eglConfig) {



        int i= carregaTextura(R.mipmap.imgtextura,vrOpengl);
        int i2 = i;
        //configura a cor que será usada para limpar a tela
        // vrOpengl.glClearColor(1.0f,0.0f,0.0f,0.0f);
        //metodo de limpar a tela
        //preenche todos os picels com a cor selecionada
        // vrOpengl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        //Chamado DUrante a criação do aplicativo 1 vez
        //Bom local para inicializar os recursos do programa
        //tempoInicial=System.currentTimeMillis();

    }

    @Override
    //Vai ser chamada quando a superficie mudar
    public void onSurfaceChanged(GL10 vrOpenGL, int largura, int altura) {
        PosLargura=largura;
        PosAltura=altura;
        float[] vetCoordenadas= {-lado,0,
                -lado,H,
                lado,0,
                lado,H


        };
        this.PosX=largura/2;
        this.PosY=altura/2;

        //Configura a area de visualização utilizada na tela do aparelho
        vrOpenGL.glViewport(0,0,largura,altura);


        //Vai ser chamado sempre que as caracteristicas da superficie mudarem

        //configura a matriz de projeção que define o volume de renderização
        //Criando matriz de projeção
        vrOpenGL.glMatrixMode(GL10.GL_PROJECTION);
        //zerando a matriz (matrix de identidade)
        vrOpenGL.glLoadIdentity();  // carrega a matriz identidade para tirar o lixo da memoria
        //setar volume de renderização
        vrOpenGL.glOrthof(0,largura, 0,altura,1,-1);

        //Setando uma matriz de modelView
        //configura a matriz de cameras e modelo
        vrOpenGL.glMatrixMode(GL10.GL_MODELVIEW);
        vrOpenGL.glLoadIdentity();
        //configura a cor que sera utilizada para limpar o fundo da tela
        vrOpenGL.glClearColor(1.0f,1.0f,1.0f,1.0f);

        //Gera Um VETOR DE VERTICES DO TIPO FLOATEBUFFER
        FloatBuffer buffer= CriaBuffer(vetCoordenadas);

        //habilitar o open gl a veceber o array
        //SOLICITAR QUE O OPENGL LIBERE O RCURSO DE ARRAY DE VERTICES
        vrOpenGL.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        //REGISTRAR O VETOR DE VERTICES CRIADO (FLOATBUFFER) NA OPENGL
        vrOpenGL.glVertexPointer(2,GL10.GL_FLOAT,0,buffer);

        //##########################################################################################
        //--------------------------------TEXTURA --------------------------------------------------

    }

    @Override
    //Esse metodo é o que vai ser chamado a todo momento
    //ele que vai ser chamado.
    public void onDrawFrame(GL10 vrOpengl) {

        vrOpengl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        //vrOpengl.glClearColor((float)Math.random(),(float)Math.random(),(float)Math.random(),1);
        //CONFIGURAR A COR ATUAL DO DESENHO
//        vrOpengl.glColor4f((float) Math.random(),(float) Math.random(),(float)Math.random(),1.0f);
        //Carrega a matriz identidade na model view
        vrOpengl.glLoadIdentity();




        //Faz a translação
        vrOpengl.glTranslatef(PosX,0,1);
        vrOpengl.glRotatef(angulo,0,0,1);
        //pinta o desenho

        vrOpengl.glColor4f(1.0f,0.0f,0.0f,1.0f);

        vrOpengl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,0,4);

    }

    public static FloatBuffer CriaBuffer (float[] array)
    {
        //alloc Bytes in memory
        ByteBuffer vrByteBuffer= ByteBuffer.allocateDirect(array.length*4);
        vrByteBuffer.order(ByteOrder.nativeOrder());

        //Create a FloatBuffer
        FloatBuffer vrFloatBuffer= vrByteBuffer.asFloatBuffer();
        vrFloatBuffer.clear();

        //insert a java array into float buffer
        vrFloatBuffer.put(array);
        //reset floatBuffer attribs
        vrFloatBuffer.flip();

        return vrFloatBuffer;
    }

    //metodo para carregar uma imagem;

    public int carregaTextura (int codImagem, GL10 vrOpenGL)
    {
        //Criar o vetor responsavel pelo armazenamento do cod da textura
        int[] vrContextura = new int[1];

        //Carregar a imagem na memoria RAM
        Bitmap vrImagem = BitmapFactory.decodeResource (vrActivity.getResources(),codImagem);

        //Socilitar a geracao do codigo ara a memoria VRAM
        vrOpenGL.glGenTextures(1,vrContextura,0);

        //Aponta a maquina OpenGL para a memoria ser trabalhada
        vrOpenGL.glBindTexture(GL10.GL_TEXTURE_2D, vrContextura[0]);


        //copia a imagem da RAM para VRAM
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,vrImagem,0);

        //Carregar os filtros
        vrOpenGL.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST); // DIMINUIR
        vrOpenGL.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);

        //Libera a memoria da imagem na ram
        vrImagem.recycle();
        return vrContextura[0];

    }
}

public class TelaPrincipal extends AppCompatActivity {
    //Cria uma variavel de referencia para a OpenGL
    GLSurfaceView superficieDesenho=null;
    Renderizador render=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Valida a variavel de referencia com uma instancia da superficie
        superficieDesenho=new GLSurfaceView(this);
        render= new Renderizador(this);
        //Ligando classe
        superficieDesenho.setRenderer(render);
        //Configura a tela do aparelho para mostrar a sup. de desenho
        setContentView(superficieDesenho);
        //IMPRIME UMA MENSAGEM NO LOG COM A TAG FPS E O TEXTO DO 2 PARAMETRO
        Log.i("FPS","Alguma coisa");

    }
}
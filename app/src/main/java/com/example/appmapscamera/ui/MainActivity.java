package com.example.appmapscamera.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.appmapscamera.R;
import com.example.appmapscamera.databinding.ActivityMainBinding;

/**
 * Activity principal da aplicação.
 * Responsável por gerenciar a navegação entre os fragments utilizando o Navigation Component
 * e integrar com a Navigation Drawer e a Toolbar personalizada.
 */
public class MainActivity extends AppCompatActivity {

    /** View Binding gerada a partir do layout activity_main.xml */
    private ActivityMainBinding binding;

    /** Controlador de navegação que gerencia os destinos e ações de navegação */
    private NavController navController;

    /** Configuração da AppBar, definindo os destinos de topo (top-level) */
    private AppBarConfiguration appBarConfiguration;

    /**
     * Método chamado quando a Activity é criada.
     * Inicializa a interface com DataBinding, configura a Toolbar como ActionBar,
     * e conecta o NavigationView (menu lateral) ao NavController.
     * @param savedInstanceState Estado anterior salvo da Activity, se existir.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa o layout utilizando o DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Configura a toolbar personalizada como ActionBar
        setSupportActionBar(binding.toolbar);

        // Obtém o NavHostFragment a partir do layout (nav_host_fragment)
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        // Verifica se o NavHostFragment foi localizado corretamente
        if (navHostFragment != null) {
            // Obtém o controlador de navegação
            navController = navHostFragment.getNavController();

            // Define quais destinos são considerados "top-level" (sem botão de retorno)
            appBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment)
                    .setOpenableLayout(binding.drawerLayout) // conecta o drawer layout
                    .build();

            // Configura a ActionBar (Toolbar) com suporte ao Navigation Drawer
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            // Conecta o menu lateral (NavigationView) ao controlador de navegação
            NavigationUI.setupWithNavController(binding.navView, navController);
        } else {
            // Caso o fragmento de navegação não seja encontrado, lança uma exceção
            throw new IllegalStateException("NavHostFragment not found");
        }
    }

    /**
     * Trata eventos de clique no botão "Up" (seta de retorno da toolbar).
     * Se o destino atual não for o topo, redireciona para o fragmento inicial (Home).
     * @return true se a navegação foi realizada com sucesso, false caso contrário.
     */
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}


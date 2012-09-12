<?php require_once('Connections/localhost.php'); ?>
<?php
mysql_select_db($database_localhost, $localhost);
$query_Recordset1 = "SELECT profesor_nombre as Nombre,
						profesor_paterno as Apellido,
						profesor_materno as Apellido2,
						ramo_nombre as Ramo,
						fecha as Fecha,
						modulo_hora_inicio as Modulo
				  FROM profesor, ramo, guardarcheckin, modulo
				  WHERE idProfe = profesor_id AND idClase = ramo_id AND Modulo_modulo_id = modulo_id";
$Recordset1 = mysql_query($query_Recordset1, $localhost) or die(mysql_error());
$row_Recordset1 = mysql_fetch_assoc($Recordset1);
$totalRows_Recordset1 = mysql_num_rows($Recordset1);
?>
<?php
echo 'Nombre;Apellido;Apellido2;Ramo;Fecha;Modulo'."\n";
?>
  <?php do { 
			echo $row_Recordset1['Nombre'].";".$row_Recordset1['Apellido'].";".$row_Recordset1['Apellido2'].";".$row_Recordset1['Ramo'].";".str_replace("-", "/", $row_Recordset1['Fecha']).";".$row_Recordset1['Modulo']."\n";
 } while ($row_Recordset1 = mysql_fetch_assoc($Recordset1)); ?>
<?php
mysql_free_result($Recordset1);
?>
